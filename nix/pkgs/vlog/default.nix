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
  version = "unstable-2021-11-01";
  src = pkgs.fetchFromGitHub {
    owner = "karmaresearch";
    repo = "vlog";
    #  rev = "v${version}";
    # 'rev' and 'sha256' point to the latest VLog master branch tag/commit we want to test
    rev = "8676c78200acccdf43da717b9b0d5b1161414652";
    sha256 = "eAmvVUVFCOI27uQwf0KYLIxK8lAwfXsKpXf/1WYdE5w=";
    # pushing an unmatching sha256 value like below will result in a nix error message with the matching value
    # sha256 = "J000000000000000000000000000000000000000000=";
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
