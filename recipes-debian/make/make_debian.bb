#
# base recipe: meta/recipes-devtools/make/make_4.2.1
# base branch: master
# base commit: a5d1288804e517dee113cb9302149541f825d316
# 

require recipes-devtools/make/make.inc

inherit debian-package
PV = "4.1"
DPR = "-9.1"
BPN = "make-dfsg"
DSC_URI = "${DEBIAN_MIRROR}/main/m/${BPN}/${BPN}_${PV}${DPR}.dsc;md5sum=7cdc9a0ed0e40cc38a6b71fb7e4cf4d1"
DEBIAN_PATCH_TYPE = "nopatch"

LICENSE = "GPLv3 & LGPLv2"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
file://tests/COPYING;md5=d32239bcb673463ab874e80d47fae504 \
file://glob/COPYING.LIB;md5=4a770b67e6be0f60da244beb2de0fce4 \
"

FILESPATH_append = ":${COREBASE}/meta/recipes-devtools/make/make"
SRC_URI += " \
            file://0001-glob-Do-not-assume-glibc-glob-internals.patch \
            file://0002-glob-Do-not-assume-glibc-glob-internals.patch \
            "

EXTRA_OECONF += "--without-guile"

BBCLASSEXTEND = "native nativesdk"
