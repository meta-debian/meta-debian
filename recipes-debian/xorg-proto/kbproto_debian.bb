#
# Base recipe: meta/recipes-graphics/xorg-proto/kbproto_1.0.6.bb
#
#

require xorg-proto-common.inc
PV = "1.0.6"

SUMMARY = "XKB: X Keyboard extension headers"

DESCRIPTION = "This package provides the wire protocol for the X \
Keyboard extension.  This extension is used to control options related \
to keyboard handling and layout."

PR = "${INC_PR}.0"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=7dd6ea99e2a83a552c02c80963623c38 \
file://XKBproto.h;beginline=1;endline=25;md5=5744eeff407aeb6e7a1346eebab486a2 \
"

BBCLASSEXTEND = "native nativesdk"

DPN = "x11proto-kb"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
