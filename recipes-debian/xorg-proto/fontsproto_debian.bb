#
# Base recipe: meta/recipes-graphics/xorg-proto/fontsproto_2.1.2.bb
# Base branch: daisy
#

require xorg-proto-common.inc
PV = "2.1.3"

SUMMARY = "XFont: X Font rasterisation headers"

DESCRIPTION = "This package provides the wire protocol for the X Font \
rasterisation extensions.  These extensions are used to control \
server-side font configurations."

PR = "${INC_PR}.0"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=c3e48aa9ce868c8e90f0401db41c11a2 \
file://FSproto.h;endline=44;md5=d2e58e27095e5ea7d4ad456ccb91986c"

BBCLASSEXTEND = "native"

DPN = "x11proto-fonts"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
