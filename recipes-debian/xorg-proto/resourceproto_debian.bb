#
# Base recipe: meta/recipes-graphics/xorg-proto/resourceproto_1.2.0.bb
# Base branch: daisy
#

require xorg-proto-common.inc
PV = "1.2.0"

SUMMARY = "XRes: X Resource extension headers"

DESCRIPTION = "This package provides the wire protocol for the X \
Resource extension.  XRes provides an interface that allows X clients to \
see and monitor X resource usage of various clients."

PR = "${INC_PR}.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=604859305e662503077240fee8c77d97"

RCONFLICTS_${PN} = "resourceext"

DPN = "x11proto-resource"

#there is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
