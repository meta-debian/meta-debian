SUMMARY = "Lex and Yacc implementation for Python"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://README.md;beginline=5;endline=32;md5=f5ee5c355c0e6719c787a71b8f0fa96c"

inherit debian-package
require recipes-debian/sources/ply.inc
DEBIAN_UNPACK_DIR = "${WORKDIR}/ply-${PV}"

do_install_append() {
	find ${D} -name *.pyc -delete
}

BBCLASSEXTEND = "native"
