#
# Base recipe: meta/recipes-graphics/xorg-lib/libxau_1.0.8.bb 
# Base branch: daisy
#

SUMMARY = "Xau: X Authority Database library"

DESCRIPTION = "libxau provides the main interfaces to the X11 \
authorisation handling, which controls authorisation for X connections, \
both client-side and server-side."

require xorg-lib-common.inc
PV = "1.0.8"

PR = "${INC_PR}.0"

inherit gettext

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=7908e342491198401321cec1956807ec"

DEPENDS += " xproto"
PROVIDES = "xau"

BBCLASSEXTEND = "native nativesdk"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"

# Correct the package name follow Debian
DEBIANNAME_${PN}-dbg = "${PN}6-dbg"
