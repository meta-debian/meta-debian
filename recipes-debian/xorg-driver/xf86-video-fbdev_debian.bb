#
# Base recipe: meta/recipes-graphics/xorg-driver/xf86-video-fbdev_0.4.4.bb
# Base branch: daisy
#

require xorg-driver-video.inc
PV = "0.4.4"

SUMMARY = "X.Org X server -- fbdev display driver"
DESCRIPTION = "fbdev is an Xorg driver for framebuffer devices. \
	       This is a non-accelerated driver."

PR = "${INC_PR}.0"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8cbd99fff773f92e844948f74ef0df8"

DPN = "xserver-xorg-video-fbdev"

# Apply debian patch by quilt
DEBIAN_PATCH_TYPE = "quilt"
