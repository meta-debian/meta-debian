#
# Base recipe: meta/recipes-graphics/xorg-lib/libxcursor_1.1.14.bb
# Base branch: daisy
#

SUMMARY = "Xcursor: X Cursor management library"

DESCRIPTION = "Xcursor is a simple library designed to help locate and \
load cursors. Cursors can be loaded from files or memory. A library of \
common cursors exists which map to the standard X cursor names. Cursors \
can exist in several sizes and the library automatically picks the best \
size."

require xorg-lib-common.inc
PV = "1.1.14"

PR = "${INC_PR}.0"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=8902e6643f7bcd7793b23dcd5d8031a4"

DEPENDS += "libxrender libxfixes"
BBCLASSEXTEND = "native"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"

DEBIANNAME_${PN}-dbg = "${PN}1-dbg"
