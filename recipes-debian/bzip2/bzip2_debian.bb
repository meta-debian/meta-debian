require recipes-extended/bzip2/${BPN}_1.0.6.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-extended/bzip2/bzip2-1.0.6:\
"

inherit debian-package
DEBIAN_SECTION = "utils"
DPR = "0"

LICENSE = "bzip2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ddeb76cd34e791893c0f539fdab879bb"

SRC_URI += " \
file://configure.ac \
file://Makefile.am \
file://run-ptest \
"

