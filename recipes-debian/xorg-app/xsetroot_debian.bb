#
# debian
#
require xorg-app-common.inc

SUMMARY = "Utility for setting various user preference options of the display"

DESCRIPTION = "a tool for tailoring the appearance of the root window"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=6ea29dbee22324787c061f039e0529de"

inherit debian-package autotools
DEBIAN_SECTION = "x11"
DPR = "0"
DPN = "x11-xserver-utils"

# Apply debian patch by quilt
DEBIAN_PATCH_TYPE = "quilt"

DEPENDS += "libxmu virtual/libx11 xbitmaps libxcursor"

S = "${DEBIAN_UNPACK_DIR}/${PN}"
