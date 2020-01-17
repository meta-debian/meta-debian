# base recipe: meta/recipes-support/libyaml/libyaml_0.2.1.bb
# base branch: warrior

SUMMARY = "LibYAML is a YAML 1.1 parser and emitter written in C."
DESCRIPTION = "LibYAML is a C library for parsing and emitting data in YAML 1.1, \
a human-readable data serialization format. "
HOMEPAGE = "http://pyyaml.org/wiki/LibYAML"
SECTION = "libs/devel"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5591701d32590f9fa94f3bfee820b634"

inherit debian-package
require recipes-debian/sources/libyaml.inc

DEBIAN_UNPACK_DIR = "${WORKDIR}/${BPN}-dist-${PV}"

DEBIAN_QUILT_PATCHES = ""

inherit autotools

BBCLASSEXTEND = "native nativesdk"
