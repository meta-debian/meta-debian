#
# Base recipe: meta/recipes-graphics/xorg-proto/dri2proto_2.8.bb
# Base branch
#

require xorg-proto-common.inc
PV = "2.8"

SUMMARY = "DRI2: Direct Rendering Infrastructure 2 headers"

DESCRIPTION = "This package provides the wire protocol for the Direct \
Rendering Ifnrastructure 2.  DIR is required for may hardware \
accelerated OpenGL drivers."

PR = "${INC_PR}.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=2e396fa91834f8786032cad2da5638f3 \
file://dri2proto.h;endline=31;md5=22f28bf68d01b533f26195e94b3ed8ca"

DPN = "x11proto-dri2"

#apply debian patch by quilt
DEBIAN_PATCH_TYPE = "quilt"
