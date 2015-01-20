require recipes-graphics/pango/pango_1.36.2.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-graphics/pango/pango:"

BPN = "pango1.0"
inherit debian-package

DEBIAN_SECTION = "libs"
DPR = "0"

LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7"

SRC_URI += " \
file://no-tests.patch \
file://multilib-fix-clean.patch \
"
