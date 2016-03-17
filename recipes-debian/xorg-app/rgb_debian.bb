#
# base recipe: meta/recipes-graphics/xorg-app/rgb_1.0.6.bb
# base branch: jethro
#

require xorg-app-common.inc

SUMMARY = "X11 color name database"
DESCRIPTION = "This package includes both the list mapping X color names \
to RGB values (rgb.txt) and, if configured to use a database for color \
lookup, the rgb program to convert the text file into the binary database \
format."

LIC_FILES_CHKSUM = "file://COPYING;md5=ef598adbe241bd0b0b9113831f6e249a"
PE = "1"

DPN = "x11-xserver-utils"
DEPENDS += "xproto util-macros"

S = "${DEBIAN_UNPACK_DIR}/${PN}"

# Apply debian patch by quilt
DEBIAN_PATCH_TYPE = "quilt"

FILES_${PN} += "${datadir}/X11"
