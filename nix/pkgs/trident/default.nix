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
  version = "unstable-2022-08-08";
  src = pkgs.fetchFromGitHub {
    owner = "karmaresearch";
    repo = "trident";
    rev = "2800c197d48c4caf4b726c79072f456a2c54e966";
    sha256 = "y+y28drGWKws7uZ6J8loF0fzVOvRu3TVtDtg0lAQCoI=";
  };

  buildInputs = [zlib sparsehash lz4];
  nativeBuildInputs = [cmake git cacert];

  cmakeFlags = [
    "-DSPARQL=1"
    "-DCMAKE_CXX_FLAGS=-w"
    "-DKOGNAC_LIB=${kognac}/lib"
    "-DKOGNAC_INC=${kognac}/share/include"
  ];
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
