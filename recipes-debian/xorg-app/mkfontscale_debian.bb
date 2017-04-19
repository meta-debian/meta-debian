#
# Base recipe: meta/recipes-graphics/xorg-app/mkfontscale_debian.bb
# Base branch: daisy
#

require xorg-app-common.inc
PV = "7.7+2"

SUMMARY = "A program to create an index of scalable font files for X"

DESCRIPTION = "For each directory argument, mkfontscale reads all of the \
scalable font files in the directory. For every font file found, an X11 \
font name (XLFD) is generated, and is written together with the file \
name to a file fonts.scale in the directory.  The resulting fonts.scale \
is used by the mkfontdir program."

PR = "${INC_PR}.0"

DEPENDS = "util-macros-native zlib libfontenc freetype xproto"

BBCLASSEXTEND = "native"

LIC_FILES_CHKSUM = "file://COPYING;md5=2e0d129d05305176d1a790e0ac1acb7f"

DPN = "xfonts-utils"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"

S = "${DEBIAN_UNPACK_DIR}/mkfontscale"
