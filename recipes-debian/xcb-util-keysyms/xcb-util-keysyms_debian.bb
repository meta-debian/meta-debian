SUMMARY = "utility libraries for X C Binding -- keysyms"
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
LIC_FILES_CHKSUM = "file://keysyms/keysyms.c;endline=30;md5=2f8de023ed823bb92f0b47900574ea9e"

DEPENDS = "libxcb"

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

inherit autotools pkgconfig distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

DEBIANNAME_${PN}-dev = "libxcb-keysyms1-dev"
