#
# Base recipe: meta/recipes-graphics/xorg-app/xset_1.2.3.bb/xset_1.2.3.bb
# Base branch: daisy
#

require xorg-app-common.inc

SUMMARY = "Utility for setting various user preference options of the display"

DESCRIPTION = "xset is a utility that is used to set various user \
preference options of the display."

PR = "${INC_PR}.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=bea81cc9827cdf1af0e12c2b8228cf8d"

CFLAGS += "-D_GNU_SOURCE"
DPN = "x11-xserver-utils"

# Apply debian patch by quilt
DEBIAN_PATCH_TYPE = "quilt"

DEPENDS += "libxext libxmu libxau"

S = "${DEBIAN_UNPACK_DIR}/${PN}"
EXTRA_OECONF = "--without-xf86misc --without-fontcache"
