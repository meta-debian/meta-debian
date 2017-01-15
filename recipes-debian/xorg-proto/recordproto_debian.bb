#
# Base recipe: meta/recipes-graphics/xorg-proto/recordproto_1.14.2.bb
# Base branch: daisy
#

require xorg-proto-common.inc
PV = "1.14.2"

SUMMARY = "XRecord: X Record extension headers"

DESCRIPTION = "This package provides the wire protocol for the X Record \
extension.  This extension is used to record and play back event \
sequences."

PR = "${INC_PR}.0"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "\
file://COPYING;md5=575827a0f554bbed332542976d5f3d40 \
file://recordproto.h;endline=19;md5=1cbb0dd45a0b060ff833901620a3e738"

RCONFLICTS_${PN} = "recordext"

DPN = "x11proto-record"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
