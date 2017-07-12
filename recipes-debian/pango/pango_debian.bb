#
# Base recipe: 
#	meta/recipes-graphics/pango/pango.inc
#	meta/recipes-graphics/pango/pango_1.36.2.bb
# Base branch: daisy
# Base commit: 9e4aad97c3b4395edeb9dc44bfad1092cdf30a47
#

SUMMARY = "Framework for layout and rendering of internationalized text"
DESCRIPTION = "Pango is a library for laying out and rendering of text, \
with an emphasis on internationalization. Pango can be used anywhere \
that text layout is needed, though most of the work on Pango so far has \
been done in the context of the GTK+ widget toolkit. Pango forms the \
core of text and font handling for GTK+-2.x."
HOMEPAGE = "http://www.pango.org/"
BUGTRACKER = "http://bugzilla.gnome.org"

PR = "r2"
DPN = "pango1.0"

X11DEPENDS = "virtual/libx11 libxft"
DEPENDS = "glib-2.0 fontconfig freetype cairo harfbuzz qemu-native"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"
PACKAGECONFIG[x11] = "--with-xft,--without-xft,${X11DEPENDS}"

BBCLASSEXTEND = "native"
DEPENDS_class-native = "glib-2.0-native cairo-native harfbuzz-native"

inherit gnomebase gtk-doc qemu debian-package
PV = "1.36.8"

LICENSE = "LGPLv2.0+"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7"

EXTRA_AUTORECONF = ""

EXTRA_OECONF = " \
    --htmldir=${docdir}/libpango1.0-doc \
    --with-included-modules \
"

# Don't let explicit-deps auto or other packages will fail when cross-build using libpango*
EXTRA_OECONF += "--enable-explicit-deps=no"

LEAD_SONAME = "libpango-1.0*"
LIBV = "1.8.0"

postinst_prologue() {
if ! [ -e $D${sysconfdir}/pango ] ; then
	mkdir -p $D${sysconfdir}/pango
fi

if [ "x$D" != "x" ]; then
	${@qemu_run_binary(d, '$D','${bindir}/${MLPREFIX}pango-querymodules')} \
		$D${libdir}/pango/${LIBV}/modules/*.so \
		> $D${sysconfdir}/pango/${MLPREFIX}pango.modules 2>/dev/null

	[ $? -ne 0 ] && exit 1

	sed -i -e "s:$D::" $D${sysconfdir}/pango/${MLPREFIX}pango.modules

	exit 0
fi
}

#Remove doc when build
do_configure_prepend() {
	sed -i -e "s/docs//" ${S}/Makefile.am
	sed -i -e "/docs\/Makefile/d" ${S}/configure.ac
	sed -i -e "/docs\/version.xml/d" ${S}/configure.ac
	sed -i -e "/GTK_DOC_CHECK/d" ${S}/configure.ac
}

do_configure_append() {
	mkdir -p ${WORKDIR}/build-static
	cd ${WORKDIR}/build-static
	oe_runconf --disable-shared --enable-static
	cd -
}

do_compile_append() {
	oe_runmake -C ${WORKDIR}/build-static
}

do_install_append () {
	# Install static libs
	oe_runmake -C ${WORKDIR}/build-static install \
		DESTDIR="${WORKDIR}/image-static"
	cp ${WORKDIR}/image-static/${libdir}/*.a ${D}${libdir}/

	if [ "${MLPREFIX}" != "" ]; then
		mv ${D}/${bindir}/pango-querymodules ${D}/${bindir}/${MLPREFIX}pango-querymodules 
	fi

	install -d ${D}${docdir}/libpango1.0-doc/pango \
	           ${D}${datadir}/gtk-doc/html
	# According to debian/libpango1.0-doc.install.in
	install -m 0644 ${S}/docs/html/* ${D}${docdir}/libpango1.0-doc/pango/

	# According to debian/libpango1.0-doc.links.in
	ln -sf ../libglib2.0-doc/glib ${D}${docdir}/libpango1.0-doc/glib
	ln -sf ../libglib2.0-doc/gobject ${D}${docdir}/libpango1.0-doc/gobject
	ln -sf ../../doc/libpango1.0-doc/pango ${D}${datadir}/gtk-doc/html/pango
}


python populate_packages_prepend () {
    prologue = d.getVar("postinst_prologue", True)

    modules_root = d.expand('${libdir}/pango/${LIBV}/modules')

    do_split_packages(d, modules_root, '^pango-(.*)\.so$', 'pango-module-%s', 'Pango module %s', prologue + '${bindir}/${MLPREFIX}pango-querymodules > /etc/pango/${MLPREFIX}pango.modules')
}

# Add more packages according to list of package build
# from Debian source
PACKAGES += "libpangocairo libpangoft2 libpangoxft"

# libpangoxft only avavailable when enable libx11 distro feature
ALLOW_EMPTY_libpangoxft = "1"

# Rearrange files into packages
FILES_${PN} = "${sysconfdir}/pango/* ${libdir}/libpango-*${SOLIBS}"
FILES_${PN}-dbg += "${libdir}/pango/${LIBV}/modules/.debug"
FILES_${PN}-dev += "${bindir}* ${libdir}/pango/${LIBV}/modules/*.la"
FILES_libpangocairo = "${libdir}/libpangocairo*${SOLIBS}"
FILES_libpangoft2 = "${libdir}/libpangoft*${SOLIBS}"
FILES_libpangoxft = "${libdir}/libpangoxft*${SOLIBS}"

# Keep compatible with meta
RPROVIDES_${PN} += "pango-modules pango-module-indic-lang \
                    pango-module-basic-fc pango-module-arabic-lang"

# Correct name of deb file
DEBIANNAME_${PN} = "libpango-1.0-0"
DEBIANNAME_${PN}-dbg = "libpango1.0-0-dbg"
DEBIANNAME_${PN}-dev = "libpango1.0-dev"
DEBIANNAME_${PN}-doc = "libpango1.0-doc"
DEBIANNAME_libpangocairo = "libpangocairo-1.0-0"
DEBIANNAME_libpangoft2 = "libpangoft2-1.0-0"
DEBIANNAME_libpangoxft = "libpangoxft-1.0-0"
