{
  pkgs,
  buildMavenRepositoryFromLockFile,
  gitignoreSource,
  curl,
  jdk,
  lib,
  lz4,
  makeWrapper,
  maven,
  sparsehash,
  stdenv,
  vlog,
}: let
  rulewerk-dependencies = buildMavenRepositoryFromLockFile {file = ../../../mvn2nix-lock.json;};
in
  stdenv.mkDerivation rec {
    pname = "rulewerk";
    version = "0.9.0-SNAPSHOT";
    src = gitignoreSource ../../..;

    modules = [
      "core"
      "vlog"
      "rdf"
      "owlapi"
      "graal"
      "parser"
      "commands"
      "examples"
      "client"
    ];

    buildInputs = [makeWrapper lz4 curl sparsehash];
    nativeBuildInputs = [maven];

    preBuild = ''
      mkdir -p $out/lib/
      mkdir -p rulewerk-vlog/lib/
      cp ${vlog}/share/java/jvlog.jar rulewerk-vlog/lib/jvlog-local.jar
      cp -PR ${rulewerk-dependencies}/* $out/lib/

      chmod -R +w $out/lib/

      # maven needs the metadata files to resolve version ranges,
      # but `buildMavenRepositoryFromLockFile` does not provide them.
      # Hack around this be generating the necessary metadata files,
      # seemingly all for dependencies of owlapi.

      cat > $out/lib/com/google/guava/guava/maven-metadata-central.xml << EOF
      <?xml version="1.0" encoding="UTF-8"?>
      <metadata>
        <groupId>com.google.guava</groupId>
        <artifactId>guava</artifactId>
        <versioning>
          <versions>
            <version>22.0</version>
          </versions>
        </versioning>
      </metadata>
      EOF

      cat > $out/lib/com/google/code/findbugs/jsr305/maven-metadata-central.xml << EOF
      <?xml version="1.0" encoding="UTF-8"?>
      <metadata>
        <groupId>com.google.code.findbugs</groupId>
        <artifactId>jsr305</artifactId>
        <versioning>
          <versions>
            <version>3.0.2</version>
          </versions>
        </versioning>
      </metadata>
      EOF

      cat > $out/lib/org/slf4j/slf4j-api/maven-metadata-central.xml << EOF
      <?xml version="1.0" encoding="UTF-8"?>
      <metadata>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <versioning>
          <versions>
            <version>1.7.25</version>
            <version>1.7.32</version>
          </versions>
        </versioning>
      </metadata>
      EOF

      mvn --offline --no-transfer-progress initialize -Pdevelopment -Dmaven.repo.local=$out/lib
    '';

    buildPhase = ''
      runHook preBuild

      mvn package -Pclient --offline -Dmaven.repo.local=$out/lib -DskipTests

      runHook postBuild
    '';

    installPhase = ''
      runHook preInstall

      mkdir -p $out/bin $out/share/java
      find $out/lib -type f -regex '.+\(\.lastUpdated\|resolver-status\.properties\|_remote\.repositories\|maven-metadata-local\.xml\)' -delete
      for module in ${toString modules}
      do
        cp ${pname}-$module/target/${pname}-$module-${version}.jar $out/share/java
      done

      cp rulewerk-client/target/standalone-rulewerk-client-${version}.jar $out/share/java
      makeWrapper ${jdk}/bin/java $out/bin/${pname} --add-flags "-jar $out/share/java/standalone-rulewerk-client-${version}.jar"
      makeWrapper ${maven}/bin/mvn $out/bin/mvn --add-flags "-DdependenciesFromNix.repo=$out/lib"

      runHook postInstall
    '';

    meta = with lib; {
      description = "A java toolkit for reasoning with existential rules";
      license = licenses.asl20;
      homepage = "https://github.com/knowsys/rulewerk";
    };
  }
