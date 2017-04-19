#
# Base recipe: meta/recipes-graphics/xorg-driver/xf86-video-vesa_2.3.3.bb
# Base branch: daisy
#

require xorg-driver-video.inc
PV = "2.3.3"

SUMMARY = "X.Org X server -- Generic Vesa video driver"

DESCRIPTION = "vesa is an Xorg driver for generic VESA video cards. It \
can drive most VESA-compatible video cards, but only makes use of the \
basic standard VESA core that is common to these cards. The driver \
supports depths 8, 15 16 and 24."

PR = "${INC_PR}.0"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=a1f0610ebdc6f314a9fa5102a8c5c1b0"

DEPENDS += "virtual/libx11 randrproto libpciaccess"

RRECOMMENDS_${PN} += "xserver-xorg-module-libint10"

DPN = "xserver-xorg-video-vesa"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
