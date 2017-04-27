SUMMARY = "C parser in Python"
DESCRIPTION = "pycparser is a complete parser of the C language, written in pure Python using\n\
the PLY parsing library. It parses C code into an AST and can serve as a\n\
front-end for C compilers or analysis tools."
HOMEPAGE = "https://github.com/eliben/pycparser"
LICENSE = "BSD-3-Clause"
SECTION = "python"
DEPENDS = "python"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d29d3ce07825100c58ca57eea171ab65"

PR = "r0"
inherit debian-package
PV = "2.10+dfsg"
DPN = "pycparser"

inherit allarch distutils

RDEPENDS_${PN} += "python-ply"

BBCLASSEXTEND = "native"
