require recipes-multimedia/libpng/libpng_1.6.8.bb

FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-multimedia/libpng/libpng:\
"

inherit debian-package
DEBIAN_SECTION = "libs"
DPR = "0"

LICENSE = "Libpng"
LIC_FILES_CHKSUM = " \
file://LICENSE;md5=c3d807a85c09ebdff087f18b4969ff96 \
file://png.h;beginline=310;endline=424;md5=b87b5e9252a3e14808a27b92912d268d \
"

#No need to apply patch because of the difference source code version
#
#SRC_URI += "file://0001-configure-lower-automake-requirement.patch"
