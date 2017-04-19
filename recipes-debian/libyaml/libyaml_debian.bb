SUMMARY = "Fast YAML 1.1 parser and emitter library"
DESCRIPTION = "LibYAML is a C library for parsing and emitting data in YAML 1.1, a \
 human-readable data serialization format."
HOMEPAGE = "http://pyyaml.org/wiki/LibYAML"

PR = "r0"
inherit debian-package
PV = "0.1.6"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6015f088759b10e0bc2bf64898d4ae17"

inherit autotools
DEBIAN_NOAUTONAME_${PN}-dev = "1"
