require recipes-core/glibc/glibc-locale.inc

PV = "2.28"

do_install_append() {
	# Only install /usr/lib/gconv and /usr/share/i18n if charset/locales/locale-code is enabled
	# if not, there will be QA Issue "Files/directories were installed but not shipped"
	if [ ${PACKAGE_NO_GCONV} != 0 ]; then
		rm -rf ${D}${libdir}/gconv ${D}${datadir}/i18n
		rmdir --ignore-fail-on-non-empty ${D}${libdir}
	fi
}
