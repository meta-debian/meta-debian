INITTAB_APPEND ??= ""
do_install_append() {
	if [ -n "${INITTAB_APPEND}" ]; then
		echo ${INITTAB_APPEND} >> ${D}${sysconfdir}/inittab
	fi
}
