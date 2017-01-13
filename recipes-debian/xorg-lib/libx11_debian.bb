#
# Base recipe: meta/recipes-graphics/xorg-lib/libx11.inc
# Base branch: daisy
#

SUMMARY = "Xlib: C Language X Interface library"

DESCRIPTION = "This package provides a client interface to the X Window \
System, otherwise known as 'Xlib'.  It provides a complete API for the \
basic functions of the window system."

require xorg-lib-common.inc
PV = "1.6.2"

PR = "${INC_PR}.0"

inherit siteinfo gettext

PROVIDES = "virtual/libx11"

LICENSE = "MIT & MIT-style & BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=172255dee66bb0151435b2d5d709fcf7"

DEPENDS += "xproto xextproto xtrans libxcb kbproto inputproto"
DEPENDS += "xproto-native"

# Follow Debian/rules
EXTRA_OECONF += "--disable-specs --without-fop --disable-silent-rules"

# The location of keysymdef.h
EXTRA_OECONF += "--with-keysymdefdir=${STAGING_INCDIR}/X11/"

PACKAGECONFIG ??= "xcms"
PACKAGECONFIG[xcms] = "--enable-xcms,--disable-xcms"

# src/util/makekeys is built natively but needs -D_GNU_SOURCE defined.
CPPFLAGS_FOR_BUILD += "-D_GNU_SOURCE"

PACKAGES =+ "${PN}-xcb ${PN}-xcb-dev ${PN}-data"

FILES_${PN} += "${datadir}/X11/XKeysymDB ${datadir}/X11/XErrorDB ${datadir}/X11/Xcms.txt"
FILES_${PN}-xcb += "${libdir}/libX11-xcb.so.*"
FILES_${PN}-xcb-dev += "${includedir}/X11/Xlib-xcb.h ${libdir}/libX11-xcb.so \
		${libdir}/pkgconfig/x11-xcb.pc ${datadir}/man/man3/XGetXCBConnection.3 \
		${datadir}/man/man3/XSetEventQueueOwner.3"
FILES_${PN}-data += "${datadir}/X11/locale ${libdir}/X11/locale ${datadir}/X11/XErrorDB"

# Multiple libx11 derivatives from from this file and are selected by virtual/libx11
# A world build should only build the correct version, not all of them.
EXCLUDE_FROM_WORLD = "1"

BBCLASSEXTEND = "native nativesdk"

# applying debian patch by quilt
DEBIAN_PATCH_TYPE = "quilt"

# Correct the package name follow Debian
DEBIANNAME_${PN}-dbg = "${PN}6-dbg"
