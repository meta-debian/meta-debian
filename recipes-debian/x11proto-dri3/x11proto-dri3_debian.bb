SUMMARY = "X11 DRI3 extension wire protocol"
DESCRIPTION = "This package provides development headers describing the wire protocol \
for the DRI3 extension, providing mechanisms to translate between direct \
rendered buffers and X pixmaps.  In conjunction with the Present extension, \
they provide a complete direct rendering solution for OpenGL or other APIs."
HOMEPAGE = "http://www.X.org"

inherit debian-package
PV = "1.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://dri3proto.h;endline=21;md5=ac90d1d416be3cb13e1d3c88cd5166bf"

DEPENDS = "util-macros"

DEBIAN_PATCH_TYPE = "nopatch"

inherit autotools pkgconfig

# ${PN} is empty so we need to tweak -dev and -dbg package dependencies
RDEPENDS_${PN}-dev = ""
RRECOMMENDS_${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"
