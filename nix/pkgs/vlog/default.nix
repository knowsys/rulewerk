{ pkgs
, cacert
, cmake
, curl
, git
, jdk
, kognac
, lib
, lz4
, sparsehash
, stdenv
, trident
, zlib
, ...
}:

stdenv.mkDerivation rec {
  pname = "vlog";
  version = "1.3.5";
  src = pkgs.fetchFromGitHub {
    owner = "karmaresearch";
    repo = "vlog";
    rev = "v${version}";
    sha256 = "sxgiTya2Qqq/mTJGr/BOIPRKDLgIdWGzeuNn5TBis4M=";
  };

  buildInputs = [ kognac trident sparsehash jdk curl lz4 ];
  nativeBuildInputs = [ cmake git cacert ];

  cmakeFlags = [
    "-DJAVA=1"
    "-DSPARQL=1"
    "-DCMAKE_CXX_FLAGS=-w"
    "-DKOGNAC_LIB=${kognac}/lib"
    "-DKOGNAC_INC=${kognac}/share/include"
    "-DTRIDENT_LIB=${trident}/lib"
    "-DTRIDENT_INC=${trident}/share/include"
  ];
  # TODO(mx): the JAR is not reproducible, as it contains the build time & date.
  patches = [ ./patches/vlog-lz4.patch ];

  postInstall = ''
    mkdir -p $out/bin
    cp ./vlog $out/bin

    mkdir -p $out/lib
    cp ./libvlog-core.so $out/lib/

    mkdir -p $out/share/java
    cp ./jvlog.jar $out/share/java
  '';

  meta = with lib; {
    description = "A reasoner for Datalog and Existential Rules";
    license = licenses.asl20;
    homepage = "https://github.crom/karmaresearch/vlog";
  };
}