SUMMARY = "utility libraries for X C Binding"
DESCRIPTION = "The xcb-util module provides a number of libraries which sit on top of \
libxcb, the core X protocol library, and some of the extension \
libraries. These experimental libraries provide convenience functions \
and interfaces which make the raw X protocol more usable. Some of the \
libraries also provide client-side code which is not strictly part of \
the X protocol but which have traditionally been provided by Xlib."
HOMEPAGE = "http://xcb.freedesktop.org"

inherit debian-package
PV = "0.4.1"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d81840e50417975d93d04c9097b86e36"

DEPENDS = "libxcb"

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

inherit autotools pkgconfig distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

PACKAGES =+ "libxcb-icccm libxcb-icccm-dev libxcb-ewmh libxcb-ewmh-dev"

FILES_libxcb-icccm = "${libdir}/libxcb-icccm${SOLIBS}"
FILES_libxcb-ewmh = "${libdir}/libxcb-ewmh${SOLIBS}"
FILES_libxcb-icccm-dev = " \
    ${includedir}/xcb/xcb_icccm.h \
    ${libdir}/libxcb-icccm${SOLIBSDEV} \
    ${libdir}/pkgconfig/xcb-icccm.pc \
"
FILES_libxcb-ewmh-dev = " \
    ${includedir}/xcb/xcb_ewmh.h \
    ${libdir}/libxcb-ewmh${SOLIBSDEV} \
    ${libdir}/pkgconfig/xcb-ewmh.pc \
"

DEBIANNAME_libxcb-icccm-dev = "libxcb-icccm4-dev"
