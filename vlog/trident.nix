{ pkgs, lz4, git, zlib, cmake, cacert, sparsehash, kognac, ... }:
pkgs.stdenv.mkDerivation {
	name = "trident-unstable-2021-04-01";
	src = pkgs.fetchgit {
		url = "git://github.com/karmaresearch/trident";
		rev = "087e90509434f84e927251c0aa8f1dd91dbb64c7";
		sha256 = "01qw93b0hvvr7vgk24d550mvrcj7lb5chrkh0y68x1mr01di2a87";
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
