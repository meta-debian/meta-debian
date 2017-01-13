#
# Base recipe: meta/recipes-graphics/xorg-proto/compositeproto_0.4.2.bb
# Base branch: daisy
#

require xorg-proto-common.inc
PV = "0.4.2"

SUMMARY = "Xcomposite: X composite extension headers"

DESCRIPTION = "This package provides the wire protocol for the X \
composite extension.  The X composite extension provides three related \
mechanisms for compositing and off-screen storage."

PR = "${INC_PR}.0"

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=2c4bfe136f4a4418ea2f2a96b7c8f3c5 \
file://composite.h;endline=43;md5=cbd44d4079053aa75930ed2f02b92926"

RCONFLICTS_${PN} = "compositeext"
BBCLASSEXTEND = "native"

DPN = "x11proto-composite"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
