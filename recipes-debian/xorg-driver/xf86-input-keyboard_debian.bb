#
# Base recipe: meta/recipes-graphics/xorg-driver/xf86-input-keyboard_1.8.0.bb
# Base branch: daisy
#

require xorg-driver-input.inc

SUMMARY = "X.Org X server -- keyboard input driver"

DESCRIPTION = "keyboard is an Xorg input driver for keyboards. The \
driver supports the standard OS-provided keyboard interface.  The driver \
functions as a keyboard input device, and may be used as the X server's \
core keyboard."

PR = "${INC_PR}.0"

LIC_FILES_CHKSUM = "file://COPYING;md5=ea2099d24ac9e316a6d4b9f20b3d4e10"

DPN = "xserver-xorg-input-keyboard"

# There is no patch files
DEBIAN_PATCH_TYPE = "quilt"
