{ pkgs, lz4, git, zlib, cmake, cacert, sparsehash, ...}:
pkgs.stdenv.mkDerivation {
	name = "kognac-unstable-2020-12-01";
	src = pkgs.fetchgit {
		url = "git://github.com/karmaresearch/kognac";
		rev = "8430b081f8d76b11fa6858f3ec31a9ea5a5cf6a9";
		sha256 = "0mhmidbmcwql5h2qjfz3yvfhp79farx5j3cbdpisimk1zmwlzxjf";
	};

	buildInputs = [ zlib sparsehash lz4 ];
	nativeBuildInputs = [ cmake git cacert ];
	outputs = [ "out" "lib" "dev" ];

	cmakeFlags = [ "-DCMAKE_CXX_FLAGS=-w" ];
	patches = [ ./kognac-lz4.patch ];

	installPhase = ''
	  mkdir -p $out
	  cp ./kognac_exec $out/

	  mkdir -p $lib
	  cp ./libkognac-core.so $lib/

	  mkdir -p $dev
	  cp -R $src/include/kognac/ $dev/
	  cp -R $src/include/zstr/ $dev/
	'';

	postFixup = ''
	  patchelf --set-rpath $lib:$(patchelf --print-rpath $out/kognac_exec) $out/kognac_exec
	'';
}
