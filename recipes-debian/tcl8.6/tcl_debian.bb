require recipes-devtools/tcltk/tcl_8.6.1.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-devtools/tcltk/tcl:"

inherit debian-package
DEBIAN_SECTION = "libs"
DPR = "0"
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

BASE_SRC_URI = "file://tcl-add-soname.patch"

SRC_URI += " \
	${BASE_SRC_URI} \
	file://fix_non_native_build_issue.patch \
	file://fix_issue_with_old_distro_glibc.patch \
	file://no_packages.patch \
	file://tcl-remove-hardcoded-install-path.patch \
	file://alter-includedir.patch \
	file://run-ptest \
"

SRC_URI_class-native = " \
	${DEBIAN_SRC_URI} \
	${BASE_SRC_URI} \
"

DEBIAN_PATCH_TYPE = "quilt"

do_install() {
	autotools_do_install install-private-headers
	ln -sf ./tclsh${VER} ${D}${bindir}/tclsh
	ln -sf tclsh8.6 ${D}${bindir}/tclsh${VER}
	sed -i "s:-L${B}:-L${STAGING_LIBDIR}:g" tclConfig.sh
	sed -i "s:${WORKDIR}:${STAGING_INCDIR}:g" tclConfig.sh
	sed -i "s:-L${libdir}:-L=${libdir}:g" tclConfig.sh
	sed -i "s:-I${includedir}:-I=${includedir}:g" tclConfig.sh
	install -d ${D}${bindir_crossscripts}
	install -m 0755 tclConfig.sh ${D}${bindir_crossscripts}
	install -m 0755 tclConfig.sh ${D}${libdir}
	cd ..
	for dir in compat generic unix
	do
		install -d ${D}${includedir}/${BPN}${VER}/$dir
		install -m 0644 ${S}/../$dir/*.h ${D}${includedir}/${BPN}${VER}/$dir/
	done
}
