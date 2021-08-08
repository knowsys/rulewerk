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

  buildPhase = ''
    runHook preBuild

    mvn package -Pclient --offline -Dmaven.repo.local=${rulewerk-dependencies}/.m2 -DskipTests

    runHook postBuild
  '';

  installPhase = ''
    runHook preInstall

    mkdir -p $out/bin $out/share/java
    ln -s ${rulewerk-dependencies}/.m2 $out/lib
    for module in ${toString modules}
    do
      cp ${pname}-$module/target/${pname}-$module-${version}.jar $out/share/java
    done

    cp rulewerk-client/target/standalone-rulewerk-client-${version}.jar $out/share/java
    makeWrapper ${jdk}/bin/java $out/bin/${pname} --add-flags "-jar $out/share/java/standalone-rulewerk-client-${version}.jar"
    makeWrapper ${maven}/bin/mvn $out/bin/maven --add-flags "--offline -Dmaven.repo.local=${rulewerk-dependencies}/.m2"

    runHook postInstall
  '';

  meta = with lib; {
    description = "A java toolkit for reasoning with existential rules";
    license = licenses.asl20;
    homepage = "https://github.com/knowsys/rulewerk";
  };
}
