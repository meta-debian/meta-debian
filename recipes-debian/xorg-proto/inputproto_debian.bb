#
# Base recipe: meta/recipes-graphics/xorg-proto/inputproto_2.3.bb
# Base branch: daisy
#

require xorg-proto-common.inc
PV = "2.3.1"

SUMMARY = "XI: X Input extension headers"

DESCRIPTION = "This package provides the wire protocol for the X Input \
extension.  The extension supports input devices other then the core X \
keyboard and pointer."

PR = "${INC_PR}.0"

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=e562cc0f6587b961f032211d8160f31e \
file://XI2proto.h;endline=48;md5=1ac1581e61188da2885cc14ff49b20be"

inherit gettext

BBCLASSEXTEND = "native nativesdk"

DPN = "x11proto-input"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
