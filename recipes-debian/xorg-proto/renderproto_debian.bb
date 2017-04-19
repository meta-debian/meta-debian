#
# Base recipe: meta/recipes-graphics/xorg-proto/renderproto_0.11.1.bb
# Base branch: daisy
#

require xorg-proto-common.inc
PV = "0.11.1"

SUMMARY = "XRender: X rendering Extension headers"

DESCRIPTION = "This package provides the wire protocol for the X \
Rendering extension.  This is the basis the image composition within the \
X window system."

PR = "${INC_PR}.0"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "\
file://COPYING;md5=f826d99765196352e6122a406cf0d024 \
file://renderproto.h;beginline=4;endline=24;md5=3e5e2851dad240b0a3a27c4776b4fd1f"

RCONFLICTS_${PN} = "renderext"

BBCLASSEXTEND = "native nativesdk"

DPN = "x11proto-render"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
