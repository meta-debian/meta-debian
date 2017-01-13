#
# Base recipe: meta/recipes-graphics/xorg-proto/randrproto_1.4.0.bb
# Base banch: daisy
#

require xorg-proto-common.inc
PV = "1.4.0"

SUMMARY = "XRandR: X Resize, Rotate and Reflect extension headers"

DESCRIPTION = "This package provides the wire protocol for the X Resize, \
Rotate and Reflect extension.  This extension provides the ability to \
resize, rotate and reflect the root window of a screen."

PR = "${INC_PR}.0"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=00426d41bd3d9267cf9bbb2df9323a5e \
file://randrproto.h;endline=30;md5=3885957c6048fdf3310ac8ba54ca2c3f \
"

RCONFLICTS_${PN} = "randrext"

BBCLASSEXTEND = "native nativesdk"

DPN = "x11proto-randr"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
