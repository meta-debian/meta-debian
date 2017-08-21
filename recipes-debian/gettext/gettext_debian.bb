#
# base recipe: meta/recipes-core/gettext/gettext_0.18.3.2.bb
# base branch: daisy
#

PR = "0"

inherit debian-package
PV = "0.19.3"

SUMMARY = "Utilities and libraries for producing multi-lingual messages"
DESCRIPTION = "GNU gettext is a set of tools that provides a framework to help other programs produce multi-lingual messages. These tools include a set of conventions about how programs should be written to support message catalogs, a directory and file naming organization for the message catalogs themselves, a runtime library supporting the retrieval of translated messages, and a few stand-alone programs to massage in various ways the sets of translatable and already translated strings."

LICENSE = "GPLv3+ & LGPL-2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = "gettext-native virtual/libiconv expat"
DEPENDS_class-native = "gettext-minimal-native"
PROVIDES = "virtual/libintl virtual/gettext"
PROVIDES_class-native = "virtual/gettext-native"
RCONFLICTS_${PN} = "proxy-libintl"

PACKAGECONFIG[msgcat-curses] = "--with-libncurses-prefix=${STAGING_LIBDIR}/..,--disable-curses,ncurses,"

LDFLAGS_prepend_libc-uclibc = " -lrt -lpthread "

inherit autotools

EXTRA_OECONF += "--without-lispdir \
                 --disable-csharp \
                 --disable-java \
                 --disable-native-java \
                 --disable-openmp \
                 --disable-acl \
                 --with-included-glib \
                 --without-emacs \
                 --without-cvs \
                 --without-git \
                 --with-included-libxml \
                 --with-included-libcroco \
                 --with-included-libunistring \
                "

acpaths = '-I ${S}/gettext-runtime/m4 \
           -I ${S}/gettext-tools/m4'


do_install_append() {
	mv ${D}${docdir}/gettext ${D}${docdir}/gettext-doc

	rm -f ${D}${libdir}/libgettextlib.so
	rm -f ${D}${libdir}/libgettextsrc.so
	find ${D} -type f -name *.la -exec rm -f {} \;
}

do_install_append_class-native () {
	rm -f ${D}${datadir}/aclocal/*
	rm -f ${D}${datadir}/gettext/config.rpath
	rm -f ${D}${datadir}/gettext/po/Makefile.in.in
	rm -f ${D}${datadir}/gettext/po/remove-potcdate.sin
}

PACKAGES =+ "autopoint ${PN}-base libgettextpo0 libasprintf0c2 libgettextpo-dev libasprintf-dev"

FILES_${PN}-dev = ""
FILES_${PN} += " \
    ${libdir}/libgettextlib-${PV}.so \
    ${libdir}/libgettextsrc-${PV}.so \
    ${libdir}/preloadable_libintl.so \
    ${datadir}/aclocal \
"
FILES_autopoint = " \
    ${bindir}/autopoint \
    ${datadir}/gettext/archive.dir.tar.xz \
"
FILES_${PN}-base = " \
    ${bindir}/envsubst \
    ${bindir}/gettext \
    ${bindir}/gettext.sh \
    ${bindir}/ngettext \
"
FILES_libgettextpo0 = "${libdir}/libgettextpo${SOLIBS}"
FILES_libasprintf0c2 = "${libdir}/libasprintf${SOLIBS}"
FILES_libgettextpo-dev = " \
    ${includedir}/gettext-po.h \
    ${libdir}/libgettextpo${SOLIBSDEV} \
"
FILES_libasprintf-dev = " \
    ${includedir}/autosprintf.h \
    ${libdir}/libasprintf${SOLIBSDEV} \
"

DEBIAN_NOAUTONAME_libasprintf0c2 = "1"

RDEPENDS_${PN}-base = "libasprintf0c2"
RDEPENDS_${PN} = "${PN}-base"
RDEPENDS_autopoint = "xz-utils"

RDEPENDS_${PN}-base_class-native = ""
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native nativesdk"

# There is no file git-version-gen in released archive, temporarily get 
# current version from ChangeLog with do-not-use-git-version-gen.patch
SRC_URI += " \
file://parallel.patch \
file://do-not-use-git-version-gen.patch \
"
