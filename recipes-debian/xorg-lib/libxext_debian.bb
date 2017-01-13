#
# Base recipe: meta-debian/recipes-debian/xorg-lib/libxext_1.3.2.bb
# Base bracnh: daisy
#

SUMMARY = "XExt: X Extension library"

DESCRIPTION = "libXext provides an X Window System client interface to \
several extensions to the X protocol.  The supported protocol extensions \
are DOUBLE-BUFFER, DPMS, Extended-Visual-Information, LBX, MIT_SHM, \
MIT_SUNDRY-NONSTANDARD, Multi-Buffering, SECURITY, SHAPE, SYNC, TOG-CUP, \
XC-APPGROUP, XC-MISC, XTEST.  libXext also provides a small set of \
utility functions to aid authors of client APIs for X protocol \
extensions."

require xorg-lib-common.inc
PV = "1.3.3"

PR = "${INC_PR}.0"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=879ce266785414bd1cbc3bc2f4d9d7c8"

DEPENDS += "xproto virtual/libx11 xextproto libxau libxdmcp"
PROVIDES = "xext"

BBCLASSEXTEND = "native nativesdk"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"

# Correct the package name follow Debian
DEBIANNAME_${PN}-dbg = "${PN}6-dbg"
