#
# Base recipe: meta/recipes-graphics/xorg-proto/glproto_1.4.17.bb
# Base branch: daisy
#

require xorg-proto-common.inc
PV = "1.4.17"

SUMMARY = "OpenGL: X OpenGL extension headers"

DESCRIPTION = "This package provides the wire protocol for the \
OpenGL-related extensions, used to enable the rendering of applications \
using OpenGL."

PR = "${INC_PR}.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=d44ed0146997856304dfbb512a59a8de \
file://glxproto.h;beginline=4;endline=32;md5=6b79c570f644363b356456e7d44471d9"

BBCLASSEXTEND = "nativesdk"

DPN = "x11proto-gl"

# Apply debian patch by quilt
DEBIAN_PATCH_TYPE = "quilt"
