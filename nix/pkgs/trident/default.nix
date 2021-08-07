{ pkgs
, cacert
, cmake
, git
, kognac
, lib
, lz4
, sparsehash
, stdenv
, zlib
, ...
}:
stdenv.mkDerivation {
  pname = "trident";
  version = "unstable-2021-07-07";
  src = pkgs.fetchFromGitHub {
    owner = "karmaresearch";
    repo = "trident";
    rev = "15d8d6754b1021175bd42c7336f2f97f38c516e3";
    sha256 = "4CC1MyH9vVgAQy1+ATg4sUuRtTrRvhWWfCRqew2ic34=";
  };

  buildInputs = [ zlib sparsehash lz4 ];
  nativeBuildInputs = [ cmake git cacert ];

  cmakeFlags = [
    "-DSPARQL=1"
    "-DCMAKE_CXX_FLAGS=-w"
    "-DKOGNAC_LIB=${kognac}/lib"
    "-DKOGNAC_INC=${kognac}/share/include"
  ];
  patches = [ ./patches/trident-lz4.patch ];

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
