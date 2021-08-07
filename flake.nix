{
  description = "Rulewerk, a java toolkit for reasoning with existential rules";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-21.05";
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

  outputs = { self, nixpkgs, flake-utils, flake-compat, gitignoresrc, ... }@inputs:
    let getJdk = pkgs: pkgs.jdk8_headless;
    in
    {
      overlay = import ./nix { inherit getJdk gitignoresrc; };
    } // (flake-utils.lib.eachDefaultSystem (system:
      let
        pkgs = import nixpkgs {
          inherit system;
          overlays = [ self.overlay ];
        };
      in
      rec {
        packages = flake-utils.lib.flattenTree {
          inherit (pkgs) kognac trident vlog rulewerk;
        };
        defaultPackage = pkgs.rulewerk;
        apps.rulewerk = flake-utils.lib.mkApp { drv = packages.rulewerk; };
        defaultApp = apps.rulewerk;
        devShell =
          let jdk = getJdk pkgs;
          in
          pkgs.mkShell {
            buildInputs = [
              jdk
              (pkgs.maven.override { inherit jdk; })
              pkgs.kognac
              pkgs.trident
              pkgs.sparsehash
              pkgs.curl
              pkgs.lz4
              pkgs.zlib
              pkgs.rulewerk
            ];
          };
      }
    ));
}
