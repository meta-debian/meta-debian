#
# Base recipe: meta/recipes-graphics/xorg-lib/libxrandr_1.4.2.bb
# Base branch: daisy
#

SUMMARY = "XRandR: X Resize, Rotate and Reflect extension library"

DESCRIPTION = "The X Resize, Rotate and Reflect Extension, called RandR \
for short, brings the ability to resize, rotate and reflect the root \
window of a screen. It is based on the X Resize and Rotate Extension as \
specified in the Proceedings of the 2001 Usenix Technical Conference \
[RANDR]."

require xorg-lib-common.inc
PV = "1.4.2"

PR = "${INC_PR}.0"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=c9d1a2118a6cd5727521db8e7a2fee69"

DEPENDS += "virtual/libx11 randrproto libxrender libxext"

BBCLASSEXTEND = "native nativesdk"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"

#Correct the package name follow Debian
DEBIANNAME_${PN}-dbg = "${PN}2-dbg"

