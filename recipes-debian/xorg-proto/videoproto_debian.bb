#
# Base recipe: meta/recipes-graphics/xorg-proto/videoproto_2.3.2.bb
# Base branch: daisy
#
require xorg-proto-common.inc
PV = "2.3.2"

SUMMARY = "Xv: X Video extension headers"

DESCRIPTION = "This package provides the wire protocol for the X Video \
extension.  This extension alows for accerlated drawing of videos."

PR = "${INC_PR}.0"

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=ce3472a119a864085fa4155cb0979a7b"

DPN = "x11proto-video"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
