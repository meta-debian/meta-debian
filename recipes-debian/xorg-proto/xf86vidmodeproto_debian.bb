#
# Base recipe: meta/recipes-graphics/xorg-proto/xf86vidmodeproto_2.3.1.bb
# Base branch: daisy
#
require xorg-proto-common.inc
PV = "2.3.1"

SUMMARY = "XFree86-VM: XFree86 video mode extension headers"

DESCRIPTION = "This package provides the wire protocol for the XFree86 \
video mode extension.  This extension allows client applications to get \
and set video mode timings."

PR = "${INC_PR}.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=499be2ff387a42f84628c35f311f1502"

RCONFLICTS_${PN} = "xxf86vmext"

DPN = "x11proto-xf86vidmode"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
