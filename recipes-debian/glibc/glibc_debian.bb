require glibc.inc

PR = "r2"

DEPENDS += "gperf-native"

GLIBC_BROKEN_LOCALES = " _ER _ET so_ET yn_ER sid_ET tr_TR mn_MN gez_ET gez_ER bn_BD te_IN es_CR.ISO-8859-1"

EXTRA_OECONF = "--enable-kernel=${OLDEST_KERNEL} \
                --without-cvs --disable-profile \
                --disable-debug --without-gd \
                --enable-clocale=gnu \
                --enable-add-ons \
                --with-headers=${STAGING_INCDIR} \
                --without-selinux \
                --enable-obsolete-rpc \
                --with-kconfig=${STAGING_BINDIR_NATIVE} \
                --disable-profile \
                --without-gd \
                --without-cvs \
                --enable-add-ons=${GLIBC_ADDONS} \
                ${GLIBC_EXTRA_OECONF}"

EXTRA_OECONF += "${@get_libc_fpu_setting(bb, d)}"

do_configure () {
	# override this function to avoid the autoconf/automake/aclocal/autoheader
	# calls for now
	# don't pass CPPFLAGS into configure, since it upsets the kernel-headers
	# version check and doesn't really help with anything
	(cd ${S} && gnu-configize) || die "failure in running gnu-configize"
	find ${S} -name "configure" | xargs touch
	CPPFLAGS="" oe_runconf
}

rpcsvc = "bootparam_prot.x nlm_prot.x rstat.x \
          yppasswd.x klm_prot.x rex.x sm_inter.x mount.x \
          rusers.x spray.x nfs_prot.x rquota.x key_prot.x"

do_compile () {
	# -Wl,-rpath-link <staging>/lib in LDFLAGS can cause breakage if another glibc is in staging
	unset LDFLAGS
	base_do_compile
	(
		cd ${S}/sunrpc/rpcsvc
		for r in ${rpcsvc}; do
			h=`echo $r|sed -e's,\.x$,.h,'`
			rm -f $h
			${B}/sunrpc/cross-rpcgen -h $r -o $h || bbwarn "${PN}: unable to generate header for $r"
		done
	)
	echo "Adjust ldd script"
	if [ -n "${RTLDLIST}" ]
	then
		prevrtld=`cat ${B}/elf/ldd | grep "^RTLDLIST=" | sed 's#^RTLDLIST="\?\([^"]*\)"\?$#\1#'`
		if [ "${prevrtld}" != "${RTLDLIST}" ]
		then
			sed -i ${B}/elf/ldd -e "s#^RTLDLIST=.*\$#RTLDLIST=\"${prevrtld} ${RTLDLIST}\"#"
		fi
	fi
}

require glibc-package.inc

do_install() {
	# Re-write do_install from libc-common.bbclass
	# to prevent install empty ld.so.conf from ${WORKDIR}
	oe_runmake install_root=${D} install
	for r in ${rpcsvc}; do
		h=`echo $r|sed -e's,\.x$,.h,'`
		install -m 0644 ${S}/sunrpc/rpcsvc/$h ${D}/${includedir}/rpcsvc/
	done
	install -d ${D}${localedir}
	make -f ${WORKDIR}/generate-supported.mk IN="${S}/localedata/SUPPORTED" OUT="${WORKDIR}/SUPPORTED"
	# get rid of some broken files...
	for i in ${GLIBC_BROKEN_LOCALES}; do
		sed -i "/$i/d" ${WORKDIR}/SUPPORTED
	done
	rm -f ${D}${sysconfdir}/rpc
	rm -rf ${D}${datadir}/zoneinfo
	rm -rf ${D}${libexecdir}/getconf

	# Install /etc/ld.so.conf.d and /etc/ld.so.conf as Debian
	mkdir -p ${D}${sysconfdir}/ld.so.conf.d
	conffile="${D}${sysconfdir}/ld.so.conf.d/${DEB_HOST_MULTIARCH}.conf"
	echo "# Multiarch support" > $conffile
	echo "${base_libdir}" >> $conffile
	echo "${libdir}" >> $conffile

	install -m 0644 ${S}/debian/local/etc/ld.so.conf ${D}${sysconfdir}/
}

SYSROOT_PREPROCESS_FUNCS += "glibc_sysroot_preprocess"
glibc_sysroot_preprocess() {
	mkdir -p ${SYSROOT_DESTDIR}${sysconfdir}
	cp -r ${D}${sysconfdir}/ld.so.conf.d ${SYSROOT_DESTDIR}${sysconfdir}/
	sed -e "s@\(^include\s*\)/etc/@\1${STAGING_DIR_TARGET}${sysconfdir}/@g" \
	    ${D}${sysconfdir}/ld.so.conf > ${SYSROOT_DESTDIR}${sysconfdir}/ld.so.conf
}

FILES_${PN}-doc += "${datadir}"
