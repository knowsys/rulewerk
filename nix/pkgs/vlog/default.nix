{
  pkgs,
  buildMavenRepositoryFromLockFile,
  cacert,
  cmake,
  curl,
  git,
  jdk,
  kognac,
  lib,
  lz4,
  maven,
  sparsehash,
  stdenv,
  trident,
  zlib,
  ...
}: let
  rulewerk-dependencies = buildMavenRepositoryFromLockFile {file = ../../../mvn2nix-lock.json;};
in
  stdenv.mkDerivation rec {
    pname = "vlog";
    version = "unstable-2022-11-25";
    src = pkgs.fetchFromGitHub {
      owner = "karmaresearch";
      repo = "vlog";
      #  rev = "v${version}";
      # 'rev' and 'sha256' point to the latest VLog master branch tag/commit we want to test
      rev = "ca63a3c6b32b0c4e5c099b645ff3d51a89212c76";
      sha256 = "uyOSE01zc+D5Fqrex/fUespBKZgh+vDaAN/vE3ZW3RY=";
    };

    buildInputs = [kognac trident sparsehash jdk curl lz4];
    nativeBuildInputs = [cmake git cacert maven];

    cmakeFlags = [
      "-DJAVA=1"
      "-DSPARQL=1"
      "-DCMAKE_CXX_FLAGS=-w"
      "-DCMAKE_SKIP_RPATH=1"
      "-DKOGNAC_LIB=${kognac}/lib"
      "-DKOGNAC_INC=${kognac}/share/include"
      "-DTRIDENT_LIB=${trident}/lib"
      "-DTRIDENT_INC=${trident}/share/include"
    ];
    patches = [./patches/vlog-lz4.patch];

    postInstall = ''
      mkdir -p $out/bin
      cp ./vlog $out/bin

      mkdir -p $out/lib
      cp ./libvlog-core.so $out/lib/

      mkdir -p $out/share/java
      mvn --offline --no-transfer-progress io.github.zlika:reproducible-build-maven-plugin:0.16:strip-jar \
        -Dreproducible.includes=./jvlog.jar \
        -Dmaven.repo.local=${rulewerk-dependencies}
      cp ./jvlog.jar $out/share/java
    '';

    meta = with lib; {
      description = "A reasoner for Datalog and Existential Rules";
      license = licenses.asl20;
      homepage = "https://github.crom/karmaresearch/vlog";
    };
  }
