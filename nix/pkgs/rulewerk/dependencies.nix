{
  pkgs,
  gitignoreSource,
  jdk,
  lib,
  maven,
  stdenv,
}:
stdenv.mkDerivation {
  name = "rulewerk-dependencies";
  nativeBuildInputs = [maven];
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
  outputHash = "sha256-5IzMlDHripuGScX+4xn0yHHN+Hcq1jW4P5n72dir7gA=";
}
