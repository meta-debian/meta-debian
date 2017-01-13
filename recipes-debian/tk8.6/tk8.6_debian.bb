SUMMARY = "Tk toolkit for Tcl and X11 v8.6 - windowing shell"
DESCRIPTION = "\
Tk is a cross-platform graphical toolkit which provides the Motif \ 
look-and-feel and is implemented using the Tcl scripting language. \
This package contains the windowing Tcl/Tk shell (wish). \
"
HOMEPAGE = "http://www.tcl.tk/"
PR = "r2"
inherit debian-package
PV = "8.6.2"

S = "${DEBIAN_UNPACK_DIR}/unix"
TCL_VER = "8.6"
LICENSE = "tcl"
LIC_FILES_CHKSUM = "\
	file://license.terms;md5=c88f99decec11afa967ad33d314f87fe"

inherit autotools-brokensep binconfig

EXTRA_OECONF_class-target = " --with-tcl=${STAGING_BINDIR_CROSS}"
EXTRA_OECONF_class-native = " --with-tcl=${STAGING_LIBDIR_NATIVE}/tcl${TCL_VER}"

#configure follow debian/rules
EXTRA_OECONF_append = "\
	TK_LIBRARY=${datadir}/tcltk/tk${TCL_VER} \
	--includedir=${includedir}/tcl${TCL_VER} \
	--enable-shared \
	--enable-threads \
	--disable-rpath \
	--enable-xft"

DEPENDS += "tcl libx11 libxt libxext"

BINCONFIG_GLOB = "*Config.sh"

do_compile() {
	sed -i -e "s:L\/usr\/lib:L${STAGING_LIBDIR}:g" ${S}/Makefile
	oe_runmake TCL_GENERIC_DIR=${STAGING_INCDIR}/tcl${TCL_VER}/tcl-private/generic
}

do_install_append() {
	#follow debian/rules:
	install -d -m 0755 ${D}${libdir}/tcltk/tk${TCL_VER}
	sed -e 's:\$dir \.\.:${libdir}:' \
		${D}${libdir}/tk${TCL_VER}/pkgIndex.tcl \
		>${D}${libdir}/tcltk/tk${TCL_VER}/pkgIndex.tcl
	rm ${D}${libdir}/tk${TCL_VER}/pkgIndex.tcl	

	install -d -m 755 ${D}${docdir}/tk${TCL_VER}-doc
	mv -f ${D}${datadir}/tcltk/tk${TCL_VER}/demos \
		${D}${docdir}/tk${TCL_VER}-doc
	rm -f ${D}${docdir}/tk${TCL_VER}-doc/license.terms

	install -d ${D}${includedir}/tcl${TCL_VER}/tk-private/generic/ttk
	cp ${S}/../generic/*.h \
		${D}${includedir}/tcl${TCL_VER}/tk-private/generic
	cp ${S}/../generic/ttk/*.h \
		${D}${includedir}/tcl${TCL_VER}/tk-private/generic/ttk
	install -d ${D}${includedir}/tcl${TCL_VER}/tk-private/unix
	cp ${S}/*.h ${D}${includedir}/tcl${TCL_VER}/tk-private/unix

	install -d ${D}${includedir}/tcl${TCL_VER}/tk-private/compat
	cp ${S}/../compat/*.h ${D}${includedir}/tcl${TCL_VER}/tk-private/compat
	
	ln -sf libtk${TCL_VER}.so ${D}${libdir}/libtk${TCL_VER}.so.0
	mv ${D}${libdir}/tkConfig.sh ${D}${libdir}/tk${TCL_VER}/
	
	install -d -m 0755 ${D}${datadir}/menu
	install -m 0644 ${S}/../debian/tk${TCL_VER}.menu \
		${D}${datadir}/menu/tk${TCL_VER}
	rm ${D}${libdir}/pkgconfig/tk.pc
}

# Fix the path in sstate
SSTATE_SCAN_FILES += "*Config.sh"

PACKAGES =+ "lib${PN}"

FILES_lib${PN} = "\
	${libdir}/tcltk/tk${TCL_VER}/pkgIndex.tcl ${libdir}/libtk${TCL_VER}.so* \
	${datadir}/tcltk/tk${TCL_VER}/*"
FILES_${PN} += "${datadir}/menu"
FILES_${PN}-dev += "${libdir}/tk${TCL_VER}/tkConfig.sh"

PKG_${PN}-dbg = "lib${PN}-dbg"

BBCLASSEXTEND = "native"
