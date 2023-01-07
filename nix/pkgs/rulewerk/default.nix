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
    version = "unstable-latest";
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

    # this prepares a local maven repository with all the dependencies
    # and our local `vlog-java` jar.
    preBuild = ''
      mkdir -p $out/lib/

      # provide a local `vlog-java` jar. This will be installed below.
      mkdir -p rulewerk-vlog/lib/
      cp ${vlog}/share/java/jvlog.jar rulewerk-vlog/lib/jvlog-local.jar

      # create a local maven repository with all the dependencies.
      # Note that we are copying symbolic links, so this will not use much space.
      cp -PR ${rulewerk-dependencies}/* $out/lib/
      # make the local repository writable.
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

      # install our local `vlog-java` jar.
      mvn --offline --no-transfer-progress initialize -Pdevelopment -Dmaven.repo.local=$out/lib
    '';

    # Actually build the rulewerk packages. Skip tests, they are run
    # as part of `checkPhase`, which we don't need to specify here,
    # since the default is to invoke `mvn verify`.
    buildPhase = ''
      runHook preBuild

      mvn package -Pclient --offline -Dmaven.repo.local=$out/lib -DskipTests

      runHook postBuild
    '';

    # Collect built jars into the local repository, and provide
    # executables for launching rulewerk and maven.
    installPhase = ''
      runHook preInstall

      # find the version number from the generated artifacts
      vers=$(basename ${pname}-core/target/${pname}-core-*.jar | cut -d'-' -f3- | sed -e 's/.jar$//')

      mkdir -p $out/bin $out/share/java
      find $out/lib -type f -regex '.+\(\.lastUpdated\|resolver-status\.properties\|_remote\.repositories\|maven-metadata-local\.xml\)' -delete
      for module in ${toString modules}
      do
        cp ${pname}-$module/target/${pname}-$module-$vers.jar $out/share/java
      done

      cp rulewerk-client/target/standalone-rulewerk-client-$vers.jar $out/share/java
      makeWrapper ${jdk}/bin/java $out/bin/${pname} --add-flags "-jar $out/share/java/standalone-rulewerk-client-$vers.jar"
      makeWrapper ${maven}/bin/mvn $out/bin/mvn --add-flags "-DdependenciesFromNix.repo=$out/lib"

      runHook postInstall
    '';

    meta = with lib; {
      description = "A java toolkit for reasoning with existential rules";
      license = licenses.asl20;
      homepage = "https://github.com/knowsys/rulewerk";
    };
  }
