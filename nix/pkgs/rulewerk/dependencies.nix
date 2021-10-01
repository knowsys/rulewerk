{ pkgs
, gitignoreSource
, jdk
, lib
, maven
, stdenv
}:

stdenv.mkDerivation {
  name = "rulewerk-dependencies";
  nativeBuildInputs = [ maven ];
  src = gitignoreSource ../../..;

  buildPhase = ''
    runHook preBuild

    mvn --no-transfer-progress go-offline:resolve-dependencies -DexcludeArtifactIds=org.semanticweb.rulewerk.vlog-java -Pclient -Dmaven.repo.local=$out/.m2

    runHook postBuild
  '';

  installPhase = ''
    runHook preInstall

    find $out/.m2 -type f -regex '.+\(\.lastUpdated\|resolver-status\.properties\|_remote\.repositories\|maven-metadata-local\.xml\)' -delete

    runHook postInstall
  '';

  outputHashAlgo = "sha256";
  outputHashMode = "recursive";
  outputHash = "sha256-w9mi8MOXeJo9RUezNSQQmy3GrEWy3HXHgvo9caFpC3U=";
}
