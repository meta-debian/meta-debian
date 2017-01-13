#
# Base recipe: meta/recipes-graphics/xorg-driver/xf86-input-evdev_2.8.2.bb
# Base branch: daisy
#

require xorg-driver-input.inc
PV = "2.9.0"

SUMMARY = "X.Org X server -- event devices (evdev) input driver"

DESCRIPTION = "evdev is an Xorg input driver for Linux's generic event \
devices. It therefore supports all input devices that the kernel knows \
about, including most mice and keyboards. \
\
The evdev driver can serve as both a pointer and a keyboard input \
device, and may be used as both the core keyboard and the core pointer. \
Multiple input devices are supported by multiple instances of this \
driver, with one Load directive for evdev in the Module section of your \
xorg.conf for each input device that will use this driver. "

PR = "${INC_PR}.0"

LIC_FILES_CHKSUM = "file://COPYING;md5=fefe33b1cf0cacba0e72e3b0fa0f0e16"

DEPENDS += "mtdev libevdev"

DPN = "xserver-xorg-input-evdev"

# There is no debian patch
DEBIAN_PATCH_TYPE = "quilt"
