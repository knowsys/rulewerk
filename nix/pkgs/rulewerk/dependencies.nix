{ pkgs
, gitignoreSource
, jdk
, lib
, maven
, stdenv
, vlog
}:

stdenv.mkDerivation {
  name = "rulewerk-dependencies";
  nativeBuildInputs = [ maven ];
  src = gitignoreSource ../../..;

  preBuild = ''
    mkdir -p rulewerk-vlog/lib/
    cp ${vlog}/share/java/jvlog.jar rulewerk-vlog/lib/jvlog-local.jar
    mvn --no-transfer-progress initialize -Pdevelopment -Dmaven.repo.local=$out/.m2
  '';

  buildPhase = ''
    runHook preBuild

    mvn --no-transfer-progress go-offline:resolve-dependencies -Pclient -Dmaven.repo.local=$out/.m2

    runHook postBuild
  '';

  installPhase = ''
    runHook preInstall

    find $out/.m2 -type f -regex '.+\(\.lastUpdated\|resolver-status\.properties\|_remote\.repositories\|maven-metadata-local\.xml\)' -delete

    runHook postInstall
  '';

  outputHashAlgo = "sha256";
  outputHashMode = "recursive";
  outputHash = "sha256-7K2jJ34YXdkQnqA2ED0Nrz3yzuaSbyKmVMKP0/A1nmQ=";
}
