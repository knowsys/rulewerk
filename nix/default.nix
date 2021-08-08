{ getJdk, gitignoresrc }: final: prev:

let
  jdk = getJdk final.pkgs;
  maven = prev.maven.override { inherit jdk; };
  gitignoreSource = (import gitignoresrc { inherit (final.pkgs) lib; }).gitignoreSource;
in
{
  kognac = final.pkgs.callPackage ./pkgs/kognac { };
  trident = final.pkgs.callPackage ./pkgs/trident { };
  vlog = final.pkgs.callPackage ./pkgs/vlog { inherit jdk maven; };
  rulewerk = final.pkgs.callPackage ./pkgs/rulewerk { inherit jdk maven gitignoreSource; };
}
