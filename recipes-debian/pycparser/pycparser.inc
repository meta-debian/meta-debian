SUMMARY = "C parser in Python"
DESCRIPTION = "pycparser is a complete parser of the C language, written in pure Python using \
the PLY parsing library. It parses C code into an AST and can serve as a \
front-end for C compilers or analysis tools."

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86f1cedb4e6410a88ce8e30b91079169"

inherit debian-package
require recipes-debian/sources/pycparser.inc
DEBIAN_UNPACK_DIR = "${WORKDIR}/pycparser-${PV}"

DEPENDS = "${PYTHON_PN}-ply-native"

do_install_append() {
	find ${D} -name *.pyc -delete
}

RDEPENDS_${PN} += "${PYTHON_PN}-ply"

BBCLASSEXTEND = "native"
