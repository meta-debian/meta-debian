#
# Base recipe: meta/recipes-graphics/xorg-proto/xcb-proto_1.10.bb
# Base branch: daisy
#

include xcb-proto.inc

PR = "${INC_PR}.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=d763b081cb10c223435b01e00dc0aba7 \
file://src/dri2.xml;beginline=2;endline=28;md5=f8763b13ff432e8597e0d610cf598e65 \
"

# apply debian patch by quilt
DEBIAN_PATCH_TYPE = "quilt"
