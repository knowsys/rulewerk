{
  description = "Rulewerk, a java toolkit for reasoning with existential rules";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-22.11";
    utils.url = "github:gytis-ivaskevicius/flake-utils-plus";
    gitignore.url = "github:hercules-ci/gitignore.nix";
    flake-compat = {
      url = "github:edolstra/flake-compat";
      flake = false;
    };
  };

  outputs = {
    self,
    nixpkgs,
    utils,
    gitignore,
    ...
  } @ inputs: let
    getJdk = pkgs: pkgs.jdk8_headless;
  in
    utils.lib.mkFlake {
      inherit self inputs;

      overlays.default = import ./nix {
        inherit getJdk;
        inherit (gitignore.lib) gitignoreSource;
      };

      sharedOverlays = [self.overlays.default];

      outputsBuilder = channels: let
        pkgs = channels.nixpkgs;
        jdk = getJdk pkgs;
        maven = pkgs.maven.override {inherit jdk;};
      in rec {
        formatter = pkgs.alejandra;

        packages = rec {
          inherit (pkgs) kognac trident vlog rulewerk;
          default = rulewerk;
        };

        apps = rec {
          rulewerk = utils.lib.mkApp {drv = packages.rulewerk;};
          default = rulewerk;
          mvn2nix = utils.lib.mkApp {
            drv = pkgs.writeShellScriptBin "mvn2nix" ''
              ${maven}/bin/mvn org.nixos.mvn2nix:mvn2nix-maven-plugin:mvn2nix
            '';
          };
        };

        devShells.default =
          pkgs.mkShell {
            buildInputs = [
              jdk
              maven
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
      };
    };
}
