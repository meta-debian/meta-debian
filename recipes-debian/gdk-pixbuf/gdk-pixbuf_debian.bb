#
# Base recipe: recipes-gnome/gdk-pixbuf/gdk-pixbuf_2.30.8.bb
# Base branch: jethro
#

SUMMARY = "Image loading library for GTK+"
HOMEPAGE = "http://www.gtk.org/"
BUGTRACKER = "https://bugzilla.gnome.org/"

PR = "r0"

inherit debian-package
PV = "2.31.1"

LICENSE = "LGPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7 \
                    file://gdk-pixbuf/gdk-pixbuf.h;endline=26;md5=72b39da7cbdde2e665329fef618e1d6b"

DEPENDS = "glib-2.0"

MAJ_VER = "${@oe.utils.trim_version("${PV}", 2)}"

#
#- extending-libinstall-dependencies.patch:
#     This patch fixes parallel install issue that lib libpixbufloader-png.la
#     depends on libgdk_pixbuf-2.0.la which will be regenerated during insta-
#     llation, if libgdk_pixbuf-2.0.la is regenerating and at the same time
#     libpixbufloader-png.la links it, the error will happen.	
#- fatal-loader.patch:
#     Fix error releated to if an environment variable is specified 
#     set the return value from main() to non-zero if the loader 
#     had errors (missing libraries, generally).       
#
SRC_URI += " \
	file://hardcoded_libtool.patch \
	file://extending-libinstall-dependencies.patch \
	file://fatal-loader.patch \
	file://run-ptest \
	"

inherit autotools pkgconfig gettext pixbufcache ptest-gnome

LIBV = "2.10.0"

GDK_PIXBUF_LOADERS ?= "jpeg2000 x11 png jpeg tiff"

PACKAGECONFIG ??= "${GDK_PIXBUF_LOADERS}"
PACKAGECONFIG_linuxstdbase = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)} ${GDK_PIXBUF_LOADERS}"
PACKAGECONFIG_class-native = "${GDK_PIXBUF_LOADERS}"

PACKAGECONFIG[png] = "--with-libpng,--without-libpng,libpng"
PACKAGECONFIG[jpeg] = "--with-libjpeg,--without-libjpeg,libjpeg-turbo"
PACKAGECONFIG[tiff] = "--with-libtiff,--without-libtiff,tiff"
PACKAGECONFIG[jpeg2000] = "--with-libjasper,--without-libjasper,jasper"

# Use GIO to sniff image format instead of trying all loaders
PACKAGECONFIG[gio-sniff] = "--enable-gio-sniffing,--disable-gio-sniffing,,shared-mime-info"
PACKAGECONFIG[x11] = "--with-x11,--without-x11,virtual/libx11"

EXTRA_OECONF = "--disable-introspection"

FILES_${PN}-xlib = "${libdir}/*pixbuf_xlib*${SOLIBS}"
ALLOW_EMPTY_${PN}-xlib = "1"

FILES_${PN} = " \
	${libdir}/gdk-pixbuf-2.0/${LIBV}/loaders/*.so \
	${libdir}/gdk-pixbuf-2.0/gdk-pixbuf-query-loaders  \
	${libdir}/lib*.so.* \
"

FILES_${PN}-dev += " \
	${bindir}/* \
	${includedir}/* \
	${libdir}/gdk-pixbuf-2.0/${LIBV}/loaders/*.la \
"

FILES_${PN}-dbg += " \
	${libdir}/.debug/* \
	${libdir}/gdk-pixbuf-2.0/.debug/* \
	${libdir}/gdk-pixbuf-2.0/${LIBV}/loaders/.debug/* \
"

# Rename package follow Debian.
PKG_${PN} = "libgdk-pixbuf2.0-0"
PKG_${PN}-dbg = "libgdk-pixbuf2.0-0-dbg"
PKG_${PN}-dev = "libgdk-pixbuf2.0-dev"
PKG_${PN}-doc = "libgdk-pixbuf2.0-doc"

do_install_append() {
	# Move gdk-pixbuf-query-loaders into libdir so it is always available
	# in multilib builds.
	mv ${D}/${bindir}/gdk-pixbuf-query-loaders ${D}/${libdir}/gdk-pixbuf-2.0/	

	# Create softlink follow Debian
	ln -sf ../lib/gdk-pixbuf-2.0/gdk-pixbuf-query-loaders \
		${D}/${bindir}/gdk-pixbuf-query-loaders	
}

do_install_append_class-native() {
	find ${D}${libdir} -name "libpixbufloader-*.la" -exec rm \{\} \;

	create_wrapper ${D}/${bindir}/gdk-pixbuf-csource \
		GDK_PIXBUF_MODULE_FILE=${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/${LIBV}/loaders.cache

	create_wrapper ${D}/${bindir}/gdk-pixbuf-pixdata \
		GDK_PIXBUF_MODULE_FILE=${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/${LIBV}/loaders.cache

	create_wrapper ${D}/${libdir}/gdk-pixbuf-2.0/gdk-pixbuf-query-loaders \
		GDK_PIXBUF_MODULE_FILE=${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/${LIBV}/loaders.cache \
		GDK_PIXBUF_MODULEDIR=${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/${LIBV}/loaders
}
BBCLASSEXTEND = "native"

SSTATEPREINSTFUNCS_append_class-native = " gdkpixbuf_sstate_preinst"
SYSROOT_PREPROCESS_FUNCS_append_class-native = " gdkpixbuf_sstate_preinst"

gdkpixbuf_sstate_preinst() {
	if [ "${BB_CURRENTTASK}" = "populate_sysroot" ]; then
		rm -rf ${STAGING_LIBDIR_NATIVE}/gdk-pixbuf-2.0/${LIBV}/*
	fi
}
