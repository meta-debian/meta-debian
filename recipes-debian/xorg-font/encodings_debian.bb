#
# Base recipe: meta/recipes-graphics/xorg-font/encodings_1.0.4.bb
# Base branch: daisy
#

SUMMARY = "The Xorg font encoding files"
DESCRIPTION = "The encodings that map to specific characters for a \
number of Xorg and common fonts."

require xorg-font-common.inc
PV = "1.0.4"
PR = "${INC_PR}.0"

LICENSE = "PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=9da93f2daf2d5572faa2bfaf0dbd9e76"

DEPENDS = "mkfontscale-native font-util-native"
RDEPENDS_${PN} = ""

inherit allarch

EXTRA_OECONF += "--with-encodingsdir=${datadir}/fonts/X11/encodings"

DPN = "xfonts-encodings"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"

SRC_URI += " \
	file://nocompiler.patch \
"
