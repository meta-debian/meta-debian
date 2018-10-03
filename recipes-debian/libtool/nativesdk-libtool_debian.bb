#
# base recipe: meta/recipes-devtools/libtool/nativesdk-libtool_2.4.6.bb
# base branch: master
# base commit: b0f2f690a3513e4c9fa30fee1b8d7ac2d7140657
#

require libtool.inc

inherit nativesdk

SRC_URI += "\
file://prefix.patch \
file://fixinstall.patch \
"

FILES_${PN} += "${datadir}/libtool/*"

do_configure_prepend () {
	# Remove any existing libtool m4 since old stale versions would break
	# any upgrade
	rm -f ${STAGING_DATADIR}/aclocal/libtool.m4
	rm -f ${STAGING_DATADIR}/aclocal/lt*.m4
}

do_install () {
	autotools_do_install
	install -d ${D}${bindir}/
	install -m 0755 ${HOST_SYS}-libtool ${D}${bindir}/
}

SYSROOT_PREPROCESS_FUNCS += "libtoolnativesdk_sysroot_preprocess"

libtoolnativesdk_sysroot_preprocess () {
	install -d ${SYSROOT_DESTDIR}${bindir_crossscripts}/
	install -m 755 ${D}${bindir}/${HOST_SYS}-libtool \
	    ${SYSROOT_DESTDIR}${bindir_crossscripts}/${HOST_SYS}-libtool
}
