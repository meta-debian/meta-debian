#
# debian
#
require xorg-proto-common.inc

SUMMARY = "X11 Big Fonts extension wire protocol"

DESCRIPTION = "This package provides the wire protocol for the XFree86 \
Big Fonts extension used to make larger font requests possible."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=e01e66e4b317088cf869bc98e6af4fb6"

PR = "r0"

inherit debian-package
DEBIAN_SECTION = "x11"
DPR = "0"
DPN = "x11proto-xf86bigfont"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"

BBCLASSEXTEND = "native"
