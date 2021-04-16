{ pkgs, lz4, git, jdk, curl, zlib, cmake, cacert, sparsehash, kognac, trident, ... }:
pkgs.stdenv.mkDerivation {
	name = "vlog";
	src = pkgs.fetchgit {
		url = "git://github.com/karmaresearch/vlog";
		rev = "7356ed98db064ee30300950441716545b819f3a1";
		sha256 = "127jykvgvikyv8nw4ih73qs6cin6ck5bfc0p53svv7hh9zn7vaj2";
	};

	buildInputs = [ kognac trident sparsehash jdk curl lz4 ];
	nativeBuildInputs = [ cmake git cacert ];
	outputs = [ "out" "lib" "dev" ];

	cmakeFlags = [ "-DJAVA=1"
				   "-DSPARQL=1"
				   "-DCMAKE_CXX_FLAGS=-w"
				   "-DKOGNAC_LIB=${kognac.lib}"
				   "-DKOGNAC_INC=${kognac.dev}"
				   "-DTRIDENT_LIB=${trident.lib}"
				   "-DTRIDENT_INC=${trident.dev}"
				 ];
	patches = [ ./vlog-lz4.patch ];

	postInstall = ''
	  mkdir -p $out
	  cp ./vlog $out/

	  mkdir -p $lib
	  cp ./libvlog-core.so $lib/

	  mkdir -p $dev
	  cp ./jvlog.jar $dev/
	'';

	postFixup = ''
	  patchelf --set-rpath $lib:$(patchelf --print-rpath $out/vlog) $out/vlog
	'';
}
