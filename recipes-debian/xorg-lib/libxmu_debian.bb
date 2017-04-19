#
# Base recipe: meta/recipes-graphics/xorg-lib/libxmu_1.1.2.bb
# Base branch: daisy
#

SUMMARY = "Xmu and Xmuu: X Miscellaneous Utility libraries"
DESCRIPTION = "The Xmu Library is a collection of miscellaneous (some \
might say random) utility functions that have been useful in building \
various applications and widgets. This library is required by the Athena \
Widgets. A subset of the functions that do not rely on the Athena \
Widgets (libXaw) or X Toolkit Instrinsics (libXt) are provided in a \
second library, libXmuu."

require xorg-lib-common.inc
PV = "1.1.2"

PR = "${INC_PR}.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=def3d8e4e9c42004f1941fa22f01dc18"

DEPENDS += "libxt libxext"
PROVIDES = "xmu"

LEAD_SONAME = "libXmu"

PACKAGES =+ "libxmuu libxmuu-dev libxmu-headers"

FILES_libxmu-headers = "${includedir}/X11/"
FILES_libxmuu = "${libdir}/libXmuu.so.*"
FILES_libxmuu-dev = "${libdir}/libXmuu.so"

BBCLASSEXTEND = "native"

#Apply debian patch by quilt
DEBIAN_PATCH_TYPE = "quilt"
