SUMMARY = "Event loop abstraction for Libraries"
DESCRIPTION = "Libverto exists to isolate libraries from the particular event loop \
chosen by an application. Libverto provides an asynchronous \
programming interface independent of any particular event loop and \
allows applications to attach this interface to whatever event loop \
they select."
HOMEPAGE = "http://fedorahosted.net/libverto"

inherit debian-package
PV = "0.2.4"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=bc8917ab981cfa6161dc29319a4038d9"

inherit autotools pkgconfig

PACKAGECONFIG ??= "glib libev"
PACKAGECONFIG[glib] = "--with-glib,--without-glib,glib-2.0"
PACKAGECONFIG[libev] = "--with-libev,--without-libev,libev"
PACKAGECONFIG[libevent] = "--with-libevent,--without-libevent,libevent"
PACKAGECONFIG[tevent] = "--with-tevent,--without-tevent,tevent"

PACKAGES =+ "${PN}-libev ${PN}-glib"

FILES_${PN}-libev = "${libdir}/libverto-libev${SOLIBS}"
FILES_${PN}-glib = "${libdir}/libverto-glib${SOLIBS}"

RDEPENDS_${PN} += "${PN}-libev"
