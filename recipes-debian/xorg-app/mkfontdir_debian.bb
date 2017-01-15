#
# Base recipe: meta/recipes-graphics/xorg-app/mkfontdir_1.0.7.bb
# Base branch: daisy
#

require xorg-app-common.inc
PV = "7.7+2"

SUMMARY = "A program to create an index of X font files in a directory"

DESCRIPTION = "For each directory argument, mkfontdir reads all of the \
font files in the directory. The font names and related data are written \
out to the files \"fonts.dir\", \"fonts.scale\", and \"fonts.alias\".  \
The X server and font server use these files to find the available font \
files."

PR = "${INC_PR}.0"

RDEPENDS_${PN} += "mkfontscale"
RDEPENDS_${PN}_class-native += "mkfontscale-native"

BBCLASSEXTEND = "native"

LIC_FILES_CHKSUM = "file://COPYING;md5=b4fcf2b90cadbfc15009b9e124dc3a3f"

DPN = "xfonts-utils"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"

S = "${DEBIAN_UNPACK_DIR}/mkfontdir"
