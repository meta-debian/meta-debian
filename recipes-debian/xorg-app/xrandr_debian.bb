#
# Base recipe: meta/recipes-graphics/xorg-app/xrandr_1.4.1.bb
# Base branch: daisy
#

require xorg-app-common.inc

SUMMARY = "XRandR: X Resize, Rotate and Reflect extension command"

DESCRIPTION = "Xrandr is used to set the size, orientation and/or \
reflection of the outputs for a screen. It can also set the screen \
size."

PR = "${INC_PR}.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=fe1608bdb33cf8c62a4438f7d34679b3"
DEPENDS += "libxrandr libxrender"

DPN = "x11-xserver-utils"

# Apply debian patch by quilt
DEBIAN_PATCH_TYPE = "quilt"

S = "${DEBIAN_UNPACK_DIR}/${PN}"
