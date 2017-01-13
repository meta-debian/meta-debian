#
# Base recipe: meta/recipes-graphics/xorg-lib/libxdmcp_1.1.1.bb
# Base branch: daisy
#

SUMMARY = "XDMCP: X Display Manager Control Protocol library"

DESCRIPTION = "The purpose of the X Display Manager Control Protocol \
(XDMCP) is to provide a uniform mechanism for an autonomous display to \
request login service from a remote host. An X terminal (screen, \
keyboard, mouse, processor, network interface) is a prime example of an \
autonomous display."

require xorg-lib-common.inc
PV = "1.1.1"

PR = "${INC_PR}.0"

inherit gettext

DEPENDS += "xproto"
PROVIDES = "xdmcp"

XORG_PN = "libXdmcp"

BBCLASSEXTEND = "native nativesdk"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=d559fb26e129626022e052a5e6e0e123"

# there is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
