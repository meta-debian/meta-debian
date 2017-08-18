#
# Base recipe: meta/recipes-graphics/xorg-app/xprop_1.2.2.bb
# Base branch: daisy
#

require xorg-app-common.inc
PV = "7.7+2"

SUMMARY = "Utility to display window and font properties of an X server"

DESCRIPTION = "The xprop utility is for displaying window and font \
properties in an X server. One window or font is selected using the \
command line arguments or possibly in the case of a window, by clicking \
on the desired window. A list of properties is then given, possibly with \
formatting information."

PR = "${INC_PR}.0"

LIC_FILES_CHKSUM = "file://COPYING;md5=e226ab8db88ac0bc0391673be40c9f91"

DEPENDS += " libxmu"

DPN = "x11-utils"

# Apply debian patch by quilt
DEBIAN_PATCH_TYPE = "quilt"

S = "${DEBIAN_UNPACK_DIR}/${BPN}"

BBCLASSEXTEND = "native"
