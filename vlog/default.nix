let pkgs = import <nixpkgs> {};

	args = { pkgs = pkgs;
			 lz4 = pkgs.lz4.override { enableStatic = true; };
			 git = pkgs.git;
			 jdk = pkgs.jdk8_headless;
			 curl = pkgs.curl;
			 zlib = pkgs.zlib;
			 cmake = pkgs.cmake;
			 cacert = pkgs.cacert;
			 sparsehash = pkgs.sparsehash;
		   };

	kognac = import ./kognac.nix args;
	trident = import ./trident.nix (args // { inherit kognac; });
	vlog = import ./vlog.nix (args // { inherit kognac; inherit trident; });

	deps = builtins.removeAttrs args [ "pkgs" ];
in
{ inherit vlog;
  inherit trident;
  inherit kognac;
  inherit deps;
}
