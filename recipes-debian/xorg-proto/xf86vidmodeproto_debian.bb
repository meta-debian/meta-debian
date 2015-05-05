#
# xf86vidmodeproto_2.3.1.bb
#
require xorg-proto-common.inc

SUMMARY = "XFree86-VM: XFree86 video mode extension headers"

DESCRIPTION = "This package provides the wire protocol for the XFree86 \
video mode extension.  This extension allows client applications to get \
and set video mode timings."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=499be2ff387a42f84628c35f311f1502"

RCONFLICTS_${PN} = "xxf86vmext"

PR = "r1"
PE = "1"

SRC_URI[md5sum] = "e793ecefeaecfeabd1aed6a01095174e"
SRC_URI[sha256sum] = "45d9499aa7b73203fd6b3505b0259624afed5c16b941bd04fcf123e5de698770"

#
# debian
#
inherit debian-package
DEBIAN_SECTION = "x11"
DPR = "0"
DPN = "x11proto-xf86vidmode"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
