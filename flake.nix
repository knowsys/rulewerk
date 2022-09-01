{
  description = "Rulewerk, a java toolkit for reasoning with existential rules";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-22.05";
    flake-utils.url = "github:numtide/flake-utils";
    flake-compat = {
      url = "github:edolstra/flake-compat";
      flake = false;
    };
    gitignoresrc = {
      url = "github:hercules-ci/gitignore.nix";
      flake = false;
    };
  };

  outputs = {
    self,
    nixpkgs,
    flake-utils,
    flake-compat,
    gitignoresrc,
    ...
  } @ inputs: let
    getJdk = pkgs: pkgs.jdk8_headless;
  in
    {
      overlays.default = import ./nix {inherit getJdk gitignoresrc;};
    }
    // (flake-utils.lib.eachDefaultSystem (system: let
      pkgs = import nixpkgs {
        inherit system;
        overlays = [self.overlays.default];
      };
    in rec {
      formatter = pkgs.alejandra;
      packages = flake-utils.lib.flattenTree {
        inherit (pkgs) kognac trident vlog rulewerk;
        default = pkgs.rulewerk;
      };
      apps = rec {
        rulewerk = flake-utils.lib.mkApp {drv = packages.rulewerk;};
        default = rulewerk;
      };
      devShells.default = let
        jdk = getJdk pkgs;
      in
        pkgs.mkShell {
          buildInputs = [
            jdk
            (pkgs.maven.override {inherit jdk;})
            pkgs.kognac
            pkgs.trident
            pkgs.sparsehash
            pkgs.curl
            pkgs.lz4
            pkgs.zlib
            pkgs.rulewerk-debug
          ];
          shellHook = ''
            export "PATH=${pkgs.rulewerk-debug}/bin:$PATH"
          '';
        };
    }));
}
