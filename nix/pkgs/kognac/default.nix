{
  pkgs,
  cacert,
  cmake,
  git,
  lib,
  lz4,
  sparsehash,
  stdenv,
  zlib,
  ...
}:
stdenv.mkDerivation {
  pname = "kognac";
  version = "unstable-2022-08-07";
  src = pkgs.fetchFromGitHub {
    owner = "karmaresearch";
    repo = "kognac";
    rev = "ec961644647e2b545cfb859148cde3dff94d317e";
    sha256 = "uliMzYkcaIf3TR2WQkM4o07M3dGF0a4/GYlWCljTlQo=";
  };

  buildInputs = [zlib sparsehash lz4];
  nativeBuildInputs = [cmake git cacert];

  cmakeFlags = ["-DCMAKE_CXX_FLAGS=-w" "-DCMAKE_SKIP_RPATH=1"];
  # this patch forces CMake to prefer our provided lz4 library.
  patches = [./patches/kognac-lz4.patch];

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
