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
  version = "unstable-2021-07-07";
  src = pkgs.fetchFromGitHub {
    owner = "karmaresearch";
    repo = "kognac";
    rev = "9b75357f87cdbfdb4561437f0000e0a0eb5394e8";
    sha256 = "+ElTpGVFz4Gqyc2lcFfjA3W0H+FaH3hInsS51tYGk7Y=";
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
