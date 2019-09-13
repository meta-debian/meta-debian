SUMMARY = "XRender: X Rendering Extension library"

DESCRIPTION = "The X Rendering Extension (Render) introduces digital \
image composition as the foundation of a new rendering model within the \
X Window System. Rendering geometric figures is accomplished by \
client-side tessellation into either triangles or trapezoids.  Text is \
drawn by loading glyphs into the server and rendering sets of them."

require ${COREBASE}/meta/recipes-graphics/xorg-lib/xorg-lib-common.inc

inherit debian-package
require recipes-debian/sources/libxrender.inc
DEBIAN_PATCH_TYPE = "nopatch"
DEBIAN_UNPACK_DIR = "${WORKDIR}/${XORG_PN}-${PV}"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8bc71986d3b9b3639f6dfd6fac8f196"

DEPENDS += "virtual/libx11 xorgproto"

XORG_PN = "libXrender"

BBCLASSEXTEND = "native nativesdk"

