SUMMARY = "XRandR: X Resize, Rotate and Reflect extension library"

DESCRIPTION = "The X Resize, Rotate and Reflect Extension, called RandR \
for short, brings the ability to resize, rotate and reflect the root \
window of a screen. It is based on the X Resize and Rotate Extension as \
specified in the Proceedings of the 2001 Usenix Technical Conference \
[RANDR]."

require ${COREBASE}/meta/recipes-graphics/xorg-lib/xorg-lib-common.inc

inherit debian-package
require recipes-debian/sources/libxrandr.inc
DEBIAN_PATCH_TYPE = "nopatch"
DEBIAN_UNPACK_DIR = "${WORKDIR}/${XORG_PN}-${PV}"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=c9d1a2118a6cd5727521db8e7a2fee69"

DEPENDS += "virtual/libx11 xorgproto libxrender libxext"

XORG_PN = "libXrandr"

BBCLASSEXTEND = "native nativesdk"
