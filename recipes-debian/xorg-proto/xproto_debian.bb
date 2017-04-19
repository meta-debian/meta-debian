#
# Base recipe: meta/recipes-graphics/xorg-proto/xproto_7.0.25.bb
# Base branch: daisy
#
require xorg-proto-common.inc
PV = "7.0.26"

SUMMARY = "Xlib: C Language X interface headers"

DESCRIPTION = "This package provides the basic headers for the X Window \
System."

PR = "${INC_PR}.0"

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=b9e051107d5628966739a0b2e9b32676"

EXTRA_OECONF_append = " --enable-specs=no"
BBCLASSEXTEND = "native nativesdk"

DPN = "x11proto-core"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
