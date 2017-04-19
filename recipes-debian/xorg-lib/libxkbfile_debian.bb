#
# Base recipe: meta/recipes-graphics/xorg-lib/libxkbfile_1.0.8.bb
# Base branch: daisy
#

SUMMARY = "XKB: X Keyboard File manipulation library"

DESCRIPTION = "libxkbfile provides an interface to read and manipulate \
description files for XKB, the X11 keyboard configuration extension."

require xorg-lib-common.inc
PV = "1.0.8"

PR = "${INC_PR}.0"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=8be7367f7e5d605a426f76bb37d4d61f"

DEPENDS += "virtual/libx11 kbproto"

BBCLASSEXTEND = "native"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"

#Correct package name follow Debian
DEBIANNAME_${PN} = "${PN}1"
DEBIANNAME_${PN}-dbg = "${PN}1-dbg"
