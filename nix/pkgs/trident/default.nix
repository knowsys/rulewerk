{
  pkgs,
  cacert,
  cmake,
  git,
  kognac,
  lib,
  lz4,
  sparsehash,
  stdenv,
  zlib,
  ...
}:
stdenv.mkDerivation {
  pname = "trident";
  version = "unstable-2022-11-25";
  src = pkgs.fetchFromGitHub {
    owner = "karmaresearch";
    repo = "trident";
    rev = "6665f4465451478119721337f65b128f868f2362";
    sha256 = "kcITwU1dVbB/sov7ZzkknSczLtTxWD9HfyFSIOOx9ak=";
  };

  buildInputs = [zlib sparsehash lz4];
  nativeBuildInputs = [cmake git cacert];

  cmakeFlags = [
    "-DSPARQL=1"
    "-DCMAKE_CXX_FLAGS=-w"
    "-DCMAKE_SKIP_RPATH=1"
    "-DKOGNAC_LIB=${kognac}/lib"
    "-DKOGNAC_INC=${kognac}/share/include"
  ];
  # this patch forces CMake to prefer our provided lz4 library.
  patches = [./patches/trident-lz4.patch];

  installPhase = ''
    runHook preInstall

    mkdir -p $out/bin
    cp ./trident $out/bin

    mkdir -p $out/lib
    cp ./libtrident-core.so $out/lib/
    cp ./libtrident-sparql.so $out/lib/

    mkdir -p $out/share/include
    cp -R $src/include/trident $out/share/include
    cp -R $src/include/layers $out/share/include
    cp -R $src/rdf3x/include $out/share/

    runHook postInstall
  '';

  meta = with lib; {
    description = "A read-only RDF triple store";
    license = licenses.asl20;
    homepage = "https://github.com/karmaresearch/trident";
  };
}
