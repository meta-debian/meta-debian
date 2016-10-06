#
# Base recipe: meta/recipes-graphics/xorg-app/xeyes_1.1.1.bb
# Base branch: daisy
#

SUMMARY = "X11 eyes that follow the mouse cursor demo"
DESCRIPTION = "Xeyes is a small X11 application that shows a pair of eyes that move to \
follow the location of the mouse cursor around the screen."

require xorg-app-common.inc

PR = "${INC_PR}.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=3ea51b365051ac32d1813a7dbaa4bfc6"

DPN = "x11-apps"

DEPENDS += "libxt libxext libxmu libxrender"

# Apply patch by quilt
DEBIAN_PATCH_TYPE = "quilt"

S = "${DEBIAN_UNPACK_DIR}/xeyes"
