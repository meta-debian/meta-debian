SUMMARY = "utility libraries for X C Binding -- atom, aux and event"
DESCRIPTION = "\
The xcb-util module provides a number of libraries which sit on top of \
libxcb, the core X protocol library, and some of the extension \
libraries. These experimental libraries provide convenience functions \ 
and interfaces which make the raw X protocol more usable. Some of the \
libraries also provide client-side code which is not strictly part of \
the X protocol but which have traditionally been provided by Xlib. \
"
HOMEPAGE = "http://xcb.freedesktop.org"
PR = "r0"
inherit debian-package
PV = "0.3.8"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
	file://src/xcb_event.h;endline=27;md5=627be355aee59e1b8ade80d5bd90fad9"
inherit autotools pkgconfig

#building documentation depend on doxygen which is not yet in meta-debian
EXTRA_OECONF += "--with-doxygen=no"

DEPENDS += "util-macros libxcb"
# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

do_install_append() {
	#remove unwanted files
	rm ${D}${libdir}/*.la
}

PKG_${PN}-dev = "lib${PN}0-dev"
RDEPENDS_${PN}-dev += "libxcb-dev"

RPROVIDES_${PN} += "lib${PN}"
RPROVIDES_${PN}-dev += "lib${PN}-dev"
