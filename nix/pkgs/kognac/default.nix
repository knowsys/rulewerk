{ pkgs
, cacert
, cmake
, git
, lib
, lz4
, sparsehash
, stdenv
, zlib
, ...
}:

stdenv.mkDerivation {
  pname = "kognac";
  version = "unstable-2021-09-30";
  src = pkgs.fetchFromGitHub {
    owner = "karmaresearch";
    repo = "kognac";
    rev = "7a909854c471afa0d80c69716321bf363add591a";
    sha256 = "Cice8nAx7UoFBnqvWexGrSbWJDeiBY6Twio3nBD4TUs=";
  };

  buildInputs = [ zlib sparsehash lz4 ];
  nativeBuildInputs = [ cmake git cacert ];

  cmakeFlags = [ "-DCMAKE_CXX_FLAGS=-w" ];
  patches = [ ./patches/kognac-lz4.patch ];

  installPhase = ''
    runHook preInstall

    mkdir -p $out/bin
    cp ./kognac_exec $out/bin

    mkdir -p $out/lib
    cp ./libkognac-core.so $out/lib/

    mkdir -p $out/share/include
    cp -R $src/include/kognac/ $out/share/include
    cp -R $src/include/zstr/ $out/share/include

    runHook postInstall
  '';

  meta = with lib; {
    description = "A library handling compressed storage of RDF triples";
    license = licenses.asl20;
    homepage = "https://github.com/karmaresearch/kognac";
  };
}
