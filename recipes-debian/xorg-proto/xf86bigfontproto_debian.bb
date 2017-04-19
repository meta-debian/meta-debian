require xorg-proto-common.inc
PV = "1.2.0"

SUMMARY = "X11 Big Fonts extension wire protocol"

DESCRIPTION = "This package provides the wire protocol for the XFree86 \
Big Fonts extension used to make larger font requests possible."

PR = "${INC_PR}.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=e01e66e4b317088cf869bc98e6af4fb6"

DPN = "x11proto-xf86bigfont"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"

BBCLASSEXTEND = "native"
