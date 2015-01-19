require recipes-support/libevent/libevent_2.0.21.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-support/libevent/libevent-2.0.21:"

inherit debian-package

DEBIAN_SECTION = "libs"
DPR = "0"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=45c5316ff684bcfe2f9f86d8b1279559"

SRC_URI += " \
file://obsolete_automake_macros.patch \
file://disable_tests.patch \
"
