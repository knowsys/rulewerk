{ pkgs, lz4, git, jdk, curl, zlib, cmake, cacert, sparsehash, kognac, trident, ... }:
pkgs.stdenv.mkDerivation {
	name = "vlog";
	version = "1.35";
	src = pkgs.fetchgit {
		url = "git://github.com/karmaresearch/vlog";
		rev = "ca0669424963765d08a63a29a0d89e27cf33ef51";
		sha256 = "10xkc8qfarz3garn2x88p064mx109vqayiijk6zslhmn4r7j465k";
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
