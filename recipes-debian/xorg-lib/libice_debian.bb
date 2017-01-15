#
# Base recipe: meta/recipes-graphics/xorg-lib/libice_1.0.8.bb
# Base branch: daisy
#

SUMMARY = "ICE: Inter-Client Exchange library"

DESCRIPTION = "The Inter-Client Exchange (ICE) protocol provides a \
generic framework for building protocols on top of reliable, byte-stream \
transport connections. It provides basic mechanisms for setting up and \
shutting down connections, for performing authentication, for \
negotiating versions, and for reporting errors. "

require xorg-lib-common.inc
PV = "1.0.9"

PR = "${INC_PR}.0"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=d162b1b3c6fa812da9d804dcf8584a93"

DEPENDS += "xproto xtrans"
PROVIDES = "ice"

BBCLASSEXTEND = "native"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"

# Correct the package name follow Debian
DEBIANNAME_${PN}-dbg = "${PN}6-dbg"
