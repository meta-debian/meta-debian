require recipes-graphics/xorg-lib/pixman_0.32.4.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-graphics/xorg-lib/pixman:"

inherit debian-package

DEBIAN_SECTION = "devel"
DPR = "0"

LICENSE = "MIT & PD"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=14096c769ae0cbb5fcb94ec468be11b3 \
file://pixman/pixman-matrix.c;endline=25;md5=ba6e8769bfaaee2c41698755af04c4be \
file://pixman/pixman-arm-neon-asm.h;endline=24;md5=9a9cc1e51abbf1da58f4d9528ec9d49b \
"
SRC_URI += " \
file://0001-ARM-qemu-related-workarounds-in-cpu-features-detecti.patch \
"
