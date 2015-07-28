require xorg-app-common.inc

SUMMARY = "X11 eyes that follow the mouse cursor demo"
DESCRIPTION = "Xeyes is a small X11 application that shows a pair of eyes that move to \
follow the location of the mouse cursor around the screen."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=3ea51b365051ac32d1813a7dbaa4bfc6"

inherit debian-package

DEBIAN_SECTION = "x11"
DPR = "0"
DPN = "x11-apps"

DEBIAN_PATCH_TYPE = "quilt"

S = "${DEBIAN_UNPACK_DIR}/xeyes"
