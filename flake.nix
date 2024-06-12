{
  description = "Rulewerk, a java toolkit for reasoning with existential rules";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-23.05";
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
    # this selects the JDK version used from a package set.
    getJdk = pkgs: pkgs.jdk8_headless;
  in
    utils.lib.mkFlake rec {
      inherit self inputs;

      overlays = rec {
        mvn2nix = inputs.mvn2nix.overlay;
        rulewerk = import ./nix {
          inherit getJdk;
          inherit (gitignore.lib) gitignoreSource;
        };
        default = inputs.nixpkgs.lib.composeManyExtensions [mvn2nix rulewerk];
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

              # skip the tests here, since we are only interested in
              # building the dependency graph for rulewerk. Tests will
              # run later as part of the rulewerk derivation. Also make sure
              # to invoke all plugins that are executed during build and test,
              # since we need to have them in the repository as well.

              MAVEN_OPTS="-DskipTests=true -DskipIT=true" \
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
                  --verbose \
                | ${pkgs.jq}/bin/jq -S '{dependencies: .dependencies | with_entries(select(.key|startswith("org.semanticweb.rulewerk") == false))}' \
                > mvn2nix-lock.json

              # the `jq` invocation above serves two purposes:
              # (i) `-S` sorts the output, so that we don't get spurious
              # changes where just the order of dependencies in the lock
              # file is different, but nothing relevant is changed, and
              # (ii) remove lock entries for `org.semanticweb.rulewerk`,
              # since those are either jars built as part of rulewerk, or
              # the `vlog-java` jar. In either case, we always want to use
              # our local version of that jar instead of something from
              # maven central. Setting up the local `vlog-java` jar happens
              # as part of the rulewerk derivation.
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
            # rulewerk-debug is like rulewerk, but with debug symbols
            # in both VLog and rulewerk enabled
            pkgs.rulewerk-debug
          ];
          # rulewerk/rulewerk-debug include a wrapper around `mvn`
          # that automatically provides the local repository with the
          # dependencies in it, make sure this is at the front of the path,
          # i.e., before the upstream `mvn`.
          shellHook = ''
            export "PATH=${pkgs.rulewerk-debug}/bin:$PATH"
          '';
        };
      };
    };
}
