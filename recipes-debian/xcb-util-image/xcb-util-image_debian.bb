SUMMARY = "utility libraries for X C Binding -- image"
DESCRIPTION = "The xcb-util module provides a number of libraries which sit on top of \
libxcb, the core X protocol library, and some of the extension \
libraries. These experimental libraries provide convenience functions \
and interfaces which make the raw X protocol more usable. Some of the \
libraries also provide client-side code which is not strictly part of \
the X protocol but which have traditionally been provided by Xlib."
HOMEPAGE = "http://xcb.freedesktop.org"

inherit debian-package
PV = "0.4.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=ea7d9d7c851fbe7d251c45669cf53579"

DEPENDS = "libxcb xcb-util"

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

inherit autotools pkgconfig distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

do_install_append() {
	# xcb_bitops.h is provided by xcb-utils
	rm -f ${D}${includedir}/xcb/xcb_bitops.h
}

DEBIANNAME_${PN}-dev = "libxcb-image0-dev"
