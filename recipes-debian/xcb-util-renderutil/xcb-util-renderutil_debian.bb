SUMMARY = "utility libraries for X C Binding -- render-util"
DESCRIPTION = "This package contains the library files needed to run software using\n\
libxcb-render-util, providing convenience functions for the Render extension.\n\
.\n\
The xcb-util module provides a number of libraries which sit on top of\n\
libxcb, the core X protocol library, and some of the extension\n\
libraries. These experimental libraries provide convenience functions\n\
and interfaces which make the raw X protocol more usable. Some of the\n\
libraries also provide client-side code which is not strictly part of\n\
the X protocol but which have traditionally been provided by Xlib."
HOMEPAGE = "http://xcb.freedesktop.org"

inherit debian-package
PV = "0.3.9"

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=215cb93f2e88d81d7be73b24debbcd7c \
    file://renderutil/glyph.c;endline=24;md5=c517c483b8d726234ec94f9169236661 \
    file://renderutil/util.c;endline=20;md5=6e0bfc44fb13298c0f4694eb70dc80d4 \
    file://renderutil/xcb_renderutil.h;endline=24;md5=d0ddab3052dd4949c93cfcb0891c96df \
"

DEPENDS = "libxcb"

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

inherit autotools pkgconfig distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

DEBIANNAME_${PN}-dev = "libxcb-render-util0-dev"
