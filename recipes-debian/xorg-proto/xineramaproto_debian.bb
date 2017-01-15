#
# Base recipe: meta/recipes-graphics/xorg-proto/xineramaproto_1.2.1.bb
# Base branch: daisy
#
require xorg-proto-common.inc
PV = "1.2.1"

SUMMARY = "Xinerama: Xinerama extension headers"

DESCRIPTION = "This package provides the wire protocol for the Xinerama \
extension.  This extension is used for retrieving information about \
physical output devices which may be combined into a single logical X \
screen."

PR = "${INC_PR}.0"


LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "\
file://COPYING;md5=3e397a5326c83d5d0ebf5b3f87163ac6 \
file://panoramiXproto.h;endline=24;md5=098e0bc089368a988092b3cbda617a57"

DPN = "x11proto-xinerama"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
