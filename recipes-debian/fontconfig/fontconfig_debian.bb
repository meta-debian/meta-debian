require recipes-graphics/fontconfig/${BPN}_2.11.0.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-graphics/fontconfig/${BPN}:"

inherit debian-package
DEBIAN_SECTION = "fonts"
DPR = "0"

LICENSE = "MIT & PD"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=7a0449e9bc5370402a94c00204beca3d \
file://configure.ac;md5=3b6a4ac6b3f1a6c39f64aae5994d0532 \
"

SRC_URI += " \
file://sysroot-arg.patch \
"
