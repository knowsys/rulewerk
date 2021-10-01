{ getJdk, gitignoresrc }: final: prev:

let
  jdk = getJdk final.pkgs;
  maven = prev.maven.override { inherit jdk; };
  gitignoreSource = (import gitignoresrc { inherit (final.pkgs) lib; }).gitignoreSource;
in
rec {
  kognac = final.pkgs.callPackage ./pkgs/kognac { };
  trident = final.pkgs.callPackage ./pkgs/trident { };
  vlog = final.pkgs.callPackage ./pkgs/vlog { inherit jdk maven; };
  rulewerk = final.pkgs.callPackage ./pkgs/rulewerk { inherit jdk maven gitignoreSource; };
  vlog-debug = final.pkgs.enableDebugging vlog;
  rulewerk-debug = final.pkgs.enableDebugging (rulewerk.override { vlog = vlog-debug; });
}
