#
# Base recipe: meta/recipes-graphics/xorg-driver/xf86-input-synaptics_1.7.3.bb
# Base branch: daisy
#

require xorg-driver-input.inc
PV = "1.8.1"

SUMMARY = "X.Org X server -- synaptics touchpad input driver"

DESCRIPTION = "synaptics is an Xorg input driver for the touchpads from \
Synaptics Incorporated. Even though these touchpads (by default, \
operating in a compatibility mode emulating a standard mouse) can be \
handled by the normal evdev or mouse drivers, this driver allows more \
advanced features of the touchpad to become available."

PR = "${INC_PR}.0"

DEPENDS += "libxi mtdev libxtst"

LIC_FILES_CHKSUM = "file://COPYING;md5=55aacd3535a741824955c5eb8f061398"

DPN = "xserver-xorg-input-synaptics"

#There is no debian patch file
DEBIAN_PATCH_TYPE = "quilt"

# Fix QA issue file not shipped to any package
FILES_${PN} += "${datadir}/X11"
