{ pkgs
, gitignoreSource
, curl
, jdk
, lib
, lz4
, makeWrapper
, maven
, sparsehash
, stdenv
, vlog
}:

let
  rulewerk-dependencies = pkgs.callPackage ./dependencies.nix { inherit gitignoreSource jdk maven; };
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

  buildInputs = [ makeWrapper lz4 curl sparsehash ];
  nativeBuildInputs = [ maven ];

  preBuild = ''
    mkdir -p $out
    mkdir -p rulewerk-vlog/lib/
    cp ${vlog}/share/java/jvlog.jar rulewerk-vlog/lib/jvlog-local.jar
    cp -R ${rulewerk-dependencies}/.m2 $out/lib
    chmod -R +w $out/lib
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
    makeWrapper ${maven}/bin/mvn $out/bin/mvn --add-flags "--offline -Dmaven.repo.local=$out/lib"

    runHook postInstall
  '';

  meta = with lib; {
    description = "A java toolkit for reasoning with existential rules";
    license = licenses.asl20;
    homepage = "https://github.com/knowsys/rulewerk";
  };
}
