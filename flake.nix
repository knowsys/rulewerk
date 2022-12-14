{
  description = "Rulewerk, a java toolkit for reasoning with existential rules";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-22.11";
    utils.url = "github:gytis-ivaskevicius/flake-utils-plus";
    gitignore = {
      url = "github:hercules-ci/gitignore.nix";
      inputs.nixpkgs.follows = "nixpkgs";
    };
    mvn2nix = {
      url = "github:fzakaria/mvn2nix";
      inputs = {
        nixpkgs.follows = "nixpkgs";
        utils.follows = "utils/flake-utils";
      };
    };
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
    mvn2nix,
    ...
  } @ inputs: let
    getJdk = pkgs: pkgs.jdk8_headless;
  in
    utils.lib.mkFlake rec {
      inherit self inputs;

      overlays.default = import ./nix {
        inherit getJdk;
        inherit (gitignore.lib) gitignoreSource;
      };

      sharedOverlays = [
        mvn2nix.overlay
        self.overlays.default
      ];

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
              ${maven}/bin/mvn clean

              MAVEN_OPTS="-DskipTests=true -DskipIT=true" \
              LD_LIBRARY_PATH="''${LD_LIBRARY_PATH:+''${LD_LIBRARY_PATH}:}${pkgs.curl.out}/lib:${pkgs.lz4.out}/lib" \
                ${pkgs.mvn2nix}/bin/mvn2nix \
                  --jdk ${jdk} \
                  --goals \
                    initialize \
                    package \
                    verify \
                    jacoco:report \
                    coveralls:help \
                    io.github.zlika:reproducible-build-maven-plugin:help \
                    org.apache.maven.plugins:maven-install-plugin:help \
                    org.apache.maven.plugins:maven-shade-plugin:help \
                  --verbose > mvn2nix-lock.json
            '';
          };
        };

        devShells.default = pkgs.mkShell {
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
            export LD_LIBRARY_PATH="''${LD_LIBRARY_PATH:+''${LD_LIBRARY_PATH}:}${pkgs.curl.out}/lib:${pkgs.lz4.out}/lib"
            export "PATH=${pkgs.rulewerk-debug}/bin:$PATH"
          '';
        };
      };
    };
}
