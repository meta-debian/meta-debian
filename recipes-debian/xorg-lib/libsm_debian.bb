#
# Base recipe: meta/recipes-graphics/xorg-lib/libsm_1.2.2.bb
# Base branch: daisy
#

SUMMARY = "SM: Session Management library"

DESCRIPTION = "The Session Management Library (SMlib) is a low-level \"C\" \
language interface to XSMP.  The purpose of the X Session Management \
Protocol (XSMP) is to provide a uniform mechanism for users to save and \
restore their sessions.  A session is a group of clients, each of which \
has a particular state."

require xorg-lib-common.inc

PR = "${INC_PR}.0"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=c0fb37f44e02bdbde80546024400728d"

DEPENDS += "libice xproto xtrans e2fsprogs"

BBCLASSEXTEND = "native"

inherit debian-package
PV = "1.2.2"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"

# Correct the package name follow Debian
DEBIANNAME_${PN}-dbg = "${PN}6-dbg"
