#
# Base recipe: meta/recipes-graphics/xorg-lib/libxrender_0.9.8.bb
# Base branch: daisy
#

SUMMARY = "XRender: X Rendering Extension library"

DESCRIPTION = "The X Rendering Extension (Render) introduces digital \
image composition as the foundation of a new rendering model within the \
X Window System. Rendering geometric figures is accomplished by \
client-side tessellation into either triangles or trapezoids.  Text is \
drawn by loading glyphs into the server and rendering sets of them."

require xorg-lib-common.inc
PV = "0.9.8"

PR = "${INC_PR}.0"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8bc71986d3b9b3639f6dfd6fac8f196"

DEPENDS += "virtual/libx11 renderproto xproto xdmcp"

BBCLASSEXTEND = "native nativesdk"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"

#Correct package name follow Debian
DEBIANNAME_{PN}-dbg = "${PN}1-dbg"
