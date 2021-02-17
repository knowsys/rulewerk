let pkgs = import <nixpkgs> {};

	maven = pkgs.maven;
	dependencies = import ./vlog;
	deps = dependencies.deps // { maven = maven; };
in pkgs.mkShell {
	buildInputsNative = [ maven deps.jdk dependencies.vlog ];
	shellHook = '''
	  mkdir -p rulewerk-vlog/lib/
	  ln -sf ${dependencies.vlog.dev}/jvlog.jar rulewerk-vlog/lib/jvlog-local.jar
	  mvn initialize -Pdevelopment
	  mvn install -DskipTests
	'';
}
