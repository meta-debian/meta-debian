SUMMARY = "X11 Distributed Multihead extension library"
DESCRIPTION = "\
libdmx is an interface to the DMX extension for X, which allows a single \
server to be set up as a proxy spanning multiple servers -- not unlike \
Xinerama across discrete physical machines.  It can be reconfigured \
on the fly to change the layout, and it is presented as a single logical \
display to clients. \
"
PR = "r0"
inherit debian-package
PV = "1.1.3"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=a3c3499231a8035efd0e004cfbd3b72a"
inherit autotools pkgconfig
# Debian's source code isn't contains patch file
DEBIAN_PATCH_TYPE = "nopatch"
DEPENDS += "util-macros x11proto-dmx libxext"

do_install_append() {
	#remove the unwanted files
	rm ${D}${libdir}/*.la
}
PKG_${PN}-dbg = "${PN}1-dbg"
RDEPENDS_${PN}-dev += "x11proto-dmx-dev libx11-dev"
