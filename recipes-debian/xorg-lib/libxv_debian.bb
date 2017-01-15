#
# Base recipe: meta/recipes-graphics/xorg-lib/libxv_1.0.10.bb
# Base branch: daisy
#

SUMMARY = "Xv: X Video extension library"

DESCRIPTION = "libXv provides an X Window System client interface to the \
X Video extension to the X protocol. The X Video extension allows for \
accelerated drawing of videos.  Hardware adaptors are exposed to \
clients, which may draw in a number of colourspaces, including YUV."

require xorg-lib-common.inc
PV = "1.0.10"

PR = "${INC_PR}.0"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=827da9afab1f727f2a66574629e0f39c"

DEPENDS += "libxext videoproto"

XORG_PN = "libXv"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
