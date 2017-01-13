#
# Base recipe: meta/recipes-graphics/xorg-lib/libxi_1.7.2.bb
# Base branch: daisy
#

require xorg-lib-common.inc
PV = "1.7.4"

SUMMARY = "XI: X Input extension library"

DESCRIPTION = "libxi is an extension to the X11 protocol to support \
input devices other than the core X keyboard and pointer.  It allows \
client programs to select input from these devices independently from \
each other and independently from the core devices."

PR = "${INC_PR}.0"

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=17b064789fab936a1c58c4e13d965b0f \
file://src/XIGetDevFocus.c;endline=23;md5=cdfb0d435a33ec57ea0d1e8e395b729f"

DEPENDS += "libxext inputproto libxfixes"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"

DEBIANNAME_${PN} = "${PN}6"
DEBIANNAME_${PN}-dbg = "${PN}6-dbg"
