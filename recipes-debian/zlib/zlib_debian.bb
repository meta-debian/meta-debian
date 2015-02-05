require recipes-core/zlib/${PN}_1.2.8.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-core/zlib/zlib-1.2.8:\
"

inherit debian-package
DEBIAN_SECTION = "libs"
DPR = "0"

LICENSE = "Zlib"
LIC_FILES_CHKSUM = " \
file://zlib.h;beginline=4;endline=20;md5=ea29cb17829cd08a5e74f2076296a2e7 \
"

SRC_URI += " \
file://remove.ldconfig.call.patch \
file://Makefile-runtests.patch \
file://run-ptest \
"
