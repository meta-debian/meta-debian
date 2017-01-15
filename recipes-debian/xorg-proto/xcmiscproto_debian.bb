#
# Base recipe: meta/recipes-graphics/xorg-proto/xcmiscproto_1.2.2.bb
# Base branch: daisy
#

require xorg-proto-common.inc
PV = "1.2.2"

SUMMARY = "XC-MISC: X XC-Miscellaneous extension headers"

DESCRIPTION = "This package provides the wire protocol for the XC-MISC \
extension, which is used to get details of XID allocations within the \
server."

PR = "${INC_PR}.0"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=09d83047c15994e05db29b423ed6662e"

BBCLASSEXTEND = "native nativesdk"

DPN = "x11proto-xcmisc"

#There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
