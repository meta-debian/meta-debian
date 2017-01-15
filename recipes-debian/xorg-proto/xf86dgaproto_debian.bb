#
# Base recipe: meta/recipes-graphics/xorg-proto/xf86dgaproto_2.1.bb
# Base branch: daisy
#
require xorg-proto-common.inc
PV = "2.1"

SUMMARY = "XFree86-DGA: XFree86 Direct Graphics Access extension headers"

DESCRIPTION = "This package provides the wire protocol for the XFree86 \
Direct Graphics Access extension. This extension allows direct graphics \
access to a framebuffer-like region, as well as relative mouse \
reporting."

PR = "${INC_PR}.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=e01e66e4b317088cf869bc98e6af4fb6"

RCONFLICTS_${PN} = "xxf86dgaext"

DPN = "x11proto-xf86dga"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
