require recipes-connectivity/bluez5/bluez5_5.15.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-connectivity/bluez5/bluez5:"

inherit debian-package
DEBIAN_SECTION = "admin"
DPR = "0"

LICENSE = "GPL-2.0 & LGPL-2.1"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e \
file://COPYING.LIB;md5=fb504b67c50331fc78734fed90fb0e09 \
"

SRC_URI += " \
file://bluetooth.conf \
"
