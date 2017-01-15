#
# Base recipe: meta/recipes-graphics/xorg-driver/xf86-input-mouse_1.9.0.bb
# Base branch: daisy
#

require xorg-driver-input.inc
PV = "1.9.1"

SUMMARY = "X.Org X server -- mouse input driver"

DESCRIPTION = "mouse is an Xorg input driver for mice. The driver \
supports most available mouse types and interfaces.  The mouse driver \
functions as a pointer input device, and may be used as the X server's \
core pointer. Multiple mice are supported by multiple instances of this \
driver."

PR = "${INC_PR}.0"

LIC_FILES_CHKSUM = "file://COPYING;md5=90ea9f90d72b6d9327dede5ffdb2a510"

DPN = "xserver-xorg-input-mouse"

#Apply patch by quilt
DEBIAN_PATCH_TYPE = "quilt"
