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
  version = "unstable-2021-09-30";
  src = pkgs.fetchFromGitHub {
    owner = "karmaresearch";
    repo = "trident";
    rev = "6f14e1b57775fd9d347ce85946e6901fd2c15344";
    sha256 = "x0eb82eUhA2g7h7Cus2ODyhwY4KIdRzvbg5aFa3iUYI=";
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
