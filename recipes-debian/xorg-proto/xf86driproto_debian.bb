#
# Base recipe: meta/recipes-graphics/xorg-proto/xf86driproto_2.1.1.bb
# Base branch: daisy
#
require xorg-proto-common.inc
PV = "2.1.1"

SUMMARY = "XFree86-DRI: XFree86 Direct Rendering Infrastructure extension headers"

DESCRIPTION = "This package provides the wire protocol for the XFree86 \
Direct Rendering Infrastructure extension.  The XFree86-DRI extension is \
used to organize direct rendering support or 3D clients and help \
arbitrate requests."

PR = "${INC_PR}.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
file://COPYING;md5=ef103b9d951e39ff7e23d386e2011fa3 \
file://xf86driproto.h;endline=35;md5=42be3d8e6d429ab79172572bb0cff544"

DPN = "x11proto-xf86dri"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
