{ pkgs, lz4, git, zlib, cmake, cacert, sparsehash, kognac, ... }:
pkgs.stdenv.mkDerivation {
	name = "trident-unstable";
	version = "2021-05-18";
	src = pkgs.fetchgit {
		url = "git://github.com/karmaresearch/trident";
		rev = "c24179a17fac7d3ec8214aff9b97b41b21e981b4";
		sha256 = "0bi0366ngk162xjll1cxys6hfynw2xksz1yr7l6hdsx0bx9qvrw4";
	};

	buildInputs = [ zlib sparsehash lz4 ];
	nativeBuildInputs = [ cmake git cacert ];
	outputs = [ "out" "lib" "dev" ];

	cmakeFlags = [ "-DSPARQL=1"
				   "-DCMAKE_CXX_FLAGS=-w"
				   "-DKOGNAC_LIB=${kognac.lib}"
				   "-DKOGNAC_INC=${kognac.dev}"
				 ];
	patches = [ ./trident-lz4.patch ];

	installPhase = ''
	  mkdir -p $out
	  cp ./trident $out/

	  mkdir -p $lib/
	  cp ./libtrident-core.so $lib/
	  cp ./libtrident-sparql.so $lib/

	  mkdir -p $dev/
	  cp -R $src/include/trident $dev/
	  cp -R $src/include/layers $dev/
	  cp -R $src/rdf3x/include $dev/
	'';

	postFixup = ''
	  patchelf --set-rpath $lib:$(patchelf --print-rpath $out/trident) $out/trident
	  patchelf --set-rpath $lib:$(patchelf --print-rpath $lib/libtrident-sparql.so) $lib/libtrident-sparql.so
	'';
}
