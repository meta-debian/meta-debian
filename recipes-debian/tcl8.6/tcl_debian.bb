#
# base recipe: meta/recipes-devtools/tcltk/tcl_8.6.1.bb
# base branch: daisy
#

PR = "r4"
inherit debian-package
PV = "8.6.2+dfsg"
DPN = "tcl8.6"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = " \
	file://../license.terms;md5=3c6f62c07835353e36f0db550ccfb65a \
	file://../compat/license.terms;md5=3c6f62c07835353e36f0db550ccfb65a \
	file://../library/license.terms;md5=3c6f62c07835353e36f0db550ccfb65a \
	file://../macosx/license.terms;md5=3c6f62c07835353e36f0db550ccfb65a \
	file://../tests/license.terms;md5=3c6f62c07835353e36f0db550ccfb65a \
	file://../win/license.terms;md5=3c6f62c07835353e36f0db550ccfb65a \
"
S = "${WORKDIR}/git/unix"

DEPENDS = "tcl-native zlib"
DEPENDS_class-native = "zlib-native"

BASE_SRC_URI = "file://tcl-add-soname.patch"

SRC_URI += " \
	${BASE_SRC_URI} \
	file://fix_non_native_build_issue.patch \
	file://fix_issue_with_old_distro_glibc.patch \
	file://no_packages.patch \
	file://run-ptest \
"

SRC_URI_class-native = " \
	${DEBIAN_SRC_URI} \
	${BASE_SRC_URI} \
"

inherit autotools ptest binconfig

VER = "8.6"

# Follow debian/rules
EXTRA_OECONF = " \
	--includedir=${includedir}/${DPN} \
	--enable-shared \
	--enable-threads \
	--enable-dll-unloading \
	--disable-rpath \
	--without-tzdata \
	--enable-man-symlinks \
	\
	TCL_LIBRARY="${datadir}/tcltk/${DPN}" \
	TCL_PACKAGE_PATH="${libdir}/tcltk ${datadir}/tcltk \
                          ${libdir}/tcltk/${DPN} ${libdir}" \
"

do_configure() {
	( cd ${S}; gnu-configize )
	oe_runconf
}

do_compile_prepend() {
	echo > ${S}/../compat/fixstrtod.c
}

do_install() {
	autotools_do_install MAN_INSTALL_DIR=${D}${mandir} \
                        MANN_INSTALL_DIR=${D}${mandir}/man3 \
                        TCL_MODULE_PATH="${libdir}/tcltk ${datadir}/tcltk" \
		install
	sed -i "s:-L${B}:-L${STAGING_LIBDIR}:g" tclConfig.sh
	sed -i "s:${WORKDIR}:${STAGING_INCDIR}:g" tclConfig.sh
	ln -sf tclsh8.6 ${D}${bindir}/tclsh

	# Follow debian/rules
	#
	# Fix up the modules
        sed -i -e's:variable paths {}:variable paths {${datadir}/tcltk/${DPN}/tcl8}:' \
            ${D}${datadir}/tcltk/${DPN}/tm.tcl
        install -d -m 755 ${D}${datadir}/tcltk/${DPN}/tcl8
        mv ${D}${datadir}/tcltk/tcl8/*/* ${D}${datadir}/tcltk/${DPN}/tcl8

	# Fix up the include files
	for dir in compat generic unix libtommath
	do
		install -d ${D}${includedir}/${DPN}/tcl-private/$dir
		install -m 0644 ${S}/../$dir/*.h ${D}${includedir}/${DPN}/tcl-private/$dir/
	done

	# Fix up the libraries
	install -d -m 755 ${D}${libdir}/${DPN}
	mv ${D}${libdir}/*.sh ${D}${libdir}/${DPN}
	chmod 755 ${D}${libdir}/${DPN}/*.sh
	rm -f ${D}${datadir}/tcltk/${DPN}/ldAix

	# Fix up the manpages.
	cd ${D}${mandir}/man1
	cat tclsh.1 | sed -e 's/(n)/(3tcl)/g' > tclsh${VER}.1 && \
	rm tclsh.1
	cd ${D}${mandir}/man3
	for f in *.[3n] ; do
		f2=$(echo $f | sed -e 's/\.[3n]/.3tcl/')
		if [ -L $f ]; then
			l=$(readlink -n $f | sed -e 's/\.[3n]/.3tcl/')
			rm $f
			ln -sf $l $f2
		else
			cat $f | sed -e 's/^\.TH \([^ ]\+\|"[^"]\+"\) [3n]/.TH \1 3tcl/' \
				-e 's/\(Tk_[0-9A-Za-z]*\)(3)/\1(3tk)/g' \
				-e 's/\([A-Z][0-9A-Za-z_]*\)(3)/\1(3tcl)/g' \
				-e 's/send(n)/send(3tk)/g' \
				-e 's/text(n)/text(3tk)/g' \
				-e 's/tk(n)/tk(3tk)/g' \
				-e 's/winfo(n)/winfo(3tk)/g' \
				-e 's/(n)/(3tcl)/g' \
				-e "s/\\N'244'/\\[^o]/g" \
				>$f2
			rm $f
		fi
	done
}

SYSROOT_PREPROCESS_FUNCS += "tcl_sysroot_preprocess"
tcl_sysroot_preprocess () {
	sed -i -e "s:^\(TCL_SRC_DIR='\)/usr/include:\1${STAGING_INCDIR}:g" \
	       -e "s:^\(TCL_BUILD_STUB_LIB_PATH='\)/usr/lib:\1${B}:g" \
	       ${SYSROOT_DESTDIR}${bindir_crossscripts}/tclConfig.sh
}

PACKAGES =+ "tcl-lib"
FILES_tcl-lib = " \
	${libdir}/libtcl8.6.so.* \
	${datadir}/tcltk \
"
FILES_${PN}-dev += "${libdir}/${DPN}/tclConfig.sh ${libdir}/${DPN}/tclooConfig.sh"

DEBIANNAME_tcl-lib = "lib${DPN}"

# isn't getting picked up by shlibs code
RDEPENDS_${PN} += "tcl-lib"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native nativesdk"

do_compile_ptest() {
	oe_runmake tcltest
}

do_install_ptest() {
	cp ${B}/tcltest ${D}${PTEST_PATH}
	cp -r ${S}/../library ${D}${PTEST_PATH}
	cp -r ${S}/../tests ${D}${PTEST_PATH}
}

# Fix some paths that might be used by Tcl extensions
BINCONFIG_GLOB = "*Config.sh"

# Fix the path in sstate
SSTATE_SCAN_FILES += "*Config.sh"
