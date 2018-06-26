require recipes-devtools/quilt/quilt.inc

inherit debian-package
PV = "0.65"
DPR = "-1"
DEBIAN_SRC_URI = "git://salsa.debian.org/debian/quilt.git;protocol=https"
SRCREV = "${PV}${DPR}"

DEBIAN_UNPACK_DIR = "${WORKDIR}/git"

FILESEXTRAPATHS =. "${COREBASE}/meta/recipes-devtools/quilt/quilt:"
SRC_URI += " \
        file://run-ptest \
        file://Makefile \
        file://test.sh \
        file://0001-tests-Allow-different-output-from-mv.patch \
"
