#
# Base recipe: meta/recipes-graphics/xorg-lib/libxtst_1.2.2.bb
# Base branch: daisy
#

require xorg-lib-common.inc
PV = "1.2.2"

SUMMARY = "XTest: X Test extension library"

DESCRIPTION = "This extension is a minimal set of client and server \
extensions required to completely test the X11 server with no user \
intervention."

PR = "${INC_PR}.0"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=bb4f89972c3869f617f61c1a79ad1952 \
file://src/XTest.c;beginline=2;endline=32;md5=b1c8c9dff842b4d5b89ca5fa32c40e99"

DEPENDS += "libxext recordproto inputproto libxi"
PROVIDES = "xtst"

# Apply debian patch by quilt
DEBIAN_PATCH_TYPE = "quilt"

# Correct the package name follow Debian.
DEBIANNAME_${PN}-dbg = "${PN}6-dbg"
