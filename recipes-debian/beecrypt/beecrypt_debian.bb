require recipes-support/beecrypt/${BPN}_4.2.1.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-support/beecrypt/${BPN}:"

inherit debian-package
DEBIAN_SECTION = "devel"
DPR = "0"

LICENSE = "GPL-2.0 & LGPL-2.1"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=9894370afd5dfe7d02b8d14319e729a1 \
file://COPYING.LIB;md5=dcf3c825659e82539645da41a7908589 \
"

SRC_URI += " \
file://disable-icu-check.patch \
file://fix-security.patch \
file://fix-for-gcc-4.7.patch \
file://run-ptest \
file://beecrypt-enable-ptest-support.patch \
"
