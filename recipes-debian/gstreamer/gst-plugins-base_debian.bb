#
# base recipe: http://cgit.openembedded.org/cgit.cgi/openembedded-core/tree\
#              /meta/recipes-multimedia/gstreamer/gst-plugins-base_0.10.36.bb?h=fido
# base branch: fido
#
require gst-plugins.inc

PR = "r9"
inherit debian-package gettext pkgconfig
#Version of gst-plugins-base
VER = "0.10"
PV = "0.10.36"
DPN = "gst-plugins-base${VER}"

LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3 \
                    file://common/coverage/coverage-report.pl;beginline=2;endline=17;md5=622921ffad8cb18ab906c56052788a3f \
                    file://COPYING.LIB;md5=55ca817ccb7d5b5b66355690e9abc605 \
                    file://gst/ffmpegcolorspace/utils.c;beginline=1;endline=20;md5=9c83a200b8e597b26ca29df20fc6ecd0"

SRC_URI += " \
        file://gst-plugins-base-tremor.patch \
        file://configure.ac-fix-subparse-plugin.patch \
"
DEPENDS += "alsa-lib libogg pango libvorbis libtheora util-linux \
	glib-2.0-native cdparanoia libvisual"
EXTRA_OECONF += "\
	--disable-freetypetest --enable-nls"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)} pango"

PACKAGECONFIG[gnomevfs] = "--enable-gnome_vfs,--disable-gnome_vfs,gnome-vfs"
PACKAGECONFIG[orc] = "--enable-orc,--disable-orc,orc"
PACKAGECONFIG[pango] = "--enable-pango,--disable-pango,pango"
PACKAGECONFIG[x11] = "--enable-x --enable-xvideo,--disable-x --disable-xvideo,virtual/libx11 libxv libsm libice"

do_configure_prepend() {
	# This m4 file contains nastiness which conflicts with libtool 2.2.2
	rm -f ${S}/m4/lib-link.m4
}

do_install_append() {
	rm ${D}${libdir}/*.la ${D}${libdir}/gstreamer-*/*.la ${D}${libdir}/*.a
	oe_runmake -C ${B}/po install-data-yes DESTDIR=${D}
}

PACKAGES =+ "\
	gstreamer${VER}-alsa libgstreamer-plugins-base${VER} \
	gstreamer${VER}-plugins-base-apps gstreamer${VER}-x"

FILES_gstreamer${VER}-alsa = "${libdir}/gstreamer-${VER}/libgstalsa.so"
FILES_libgstreamer-plugins-base${VER} = "${libdir}/*.so.* \
	${datadir}/${PN}/license-translations.dict"
FILES_gstreamer${VER}-plugins-base-apps = "${bindir}/*"
FILES_gstreamer${VER}-x = "\
	${libdir}/gstreamer-${VER}/libgstpango.so \
	${libdir}/gstreamer-${VER}/libgstximagesink.so \
	${libdir}/gstreamer-${VER}/libgstxvimagesink.so"
FILES_${PN} += "${libdir}/gstreamer-${VER}/*.so"
FILES_${PN}-dbg += "${libdir}/gstreamer-${VER}/.debug"

PKG_${PN} = "gstreamer${VER}-plugins-base"
PKG_${PN}-doc = "gstreamer${VER}-plugins-base-doc"
PKG_${PN}-dbg = "gstreamer${VER}-plugins-base-dbg"
PKG_${PN}-dev = "libgstreamer-plugins-base${VER}-dev"
PKG_libgstreamer-plugins-base${VER} = "libgstreamer-plugins-base${VER}-0"
