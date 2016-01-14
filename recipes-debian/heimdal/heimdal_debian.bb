Description="Heimdal is a Kerberos 5 implementation."

PR = "r0"

inherit debian-package autotools-brokensep

LICENSE = "PD & BSD-3-Clause"
LIC_FILES_CHKSUM = "\
file://LICENSE;md5=d2c6f8cfe82d4fdd74355866f0c14d3f \
file://lib/hcrypto/libtommath/LICENSE;md5=4f6fbdd737299a6d5dac1428f38422c8"

# Makefile_debian.patch
#   patch file for cross-compiling.
SRC_URI += "file://Makefile_debian.patch"

DEPENDS += "libjson-perl heimdal-native db libxt e2fsprogs"

EXTRA_OECONF += "\
	--libexecdir="${sbindir}" \
	--prefix="${prefix}" \
	--enable-kcm \
	--without-openssl \
	--infodir="${datadir}/info" \
	--datarootdir="${datadir}" \
	--libdir="${libdir}" \
	--without-krb4 \
	--with-cross-tools=${STAGING_BINDIR_NATIVE} \ 
	ac_cv_func_getpwnam_r_posix=yes \
"

# Using perl command from sysroot instead of host 
do_configure_prepend() {
	sed -i -e "s:##perl##:${STAGING_BINDIR_NATIVE}/perl-native/perl:g" ${B}/lib/asn1/Makefile.am
	sed -i -e "s:##perl##:${STAGING_BINDIR_NATIVE}/perl-native/perl:g" ${B}/lib/hx509/Makefile.am
	sed -i -e "s:##perl##:${STAGING_BINDIR_NATIVE}/perl-native/perl:g" ${B}/kdc/Makefile.am
	sed -i -e "s:##perl##:${STAGING_BINDIR_NATIVE}/perl-native/perl:g" ${B}/appl/login/Makefile.am
	sed -i -e "s:##perl##:${STAGING_BINDIR_NATIVE}/perl-native/perl:g" ${B}/lib/krb5/Makefile.am
	sed -i -e "s:##perl##:${STAGING_BINDIR_NATIVE}/perl-native/perl:g" ${B}/lib/ntlm/Makefile.am
	sed -i -e "s:##perl##:${STAGING_BINDIR_NATIVE}/perl-native/perl:g" ${B}/lib/hdb/Makefile.am
	sed -i -e "s:##perl##:${STAGING_BINDIR_NATIVE}/perl-native/perl:g" ${B}/lib/kadm5/Makefile.am
	sed -i -e "s:##perl##:${STAGING_BINDIR_NATIVE}/perl-native/perl:g" ${B}/lib/gssapi/Makefile.am
	sed -i -e "s:##perl##:${STAGING_BINDIR_NATIVE}/perl-native/perl:g" ${B}/kcm/Makefile.am
	sed -i -e "s:##compile_et##:${STAGING_BINDIR_NATIVE}/:g" ${B}/configure.ac
	sed -i -e "s:##compile_et##:${STAGING_BINDIR_NATIVE}/:g" ${B}/cf/check-compile-et.m4
}

do_install_append() {
	# Remove unwanted files
	rm -rf ${D}${includedir}/com_err.h
	rm -rf ${D}${libdir}/libcom_err.a
	
	install -d ${D}${datadir}/heimdal-kdc
	cp ${S}/debian/extras/kdc.conf ${D}${datadir}/heimdal-kdc
	cp ${S}/debian/extras/kadmind.acl ${D}${datadir}/heimdal-kdc
	
	mkdir -p ${D}${infodir}
	mkdir -p ${D}${libdir}/${BPN}
	mv ${D}${libdir}/*.a ${D}${libdir}/${BPN}
	#mv ${D}${libdir}/*.so ${D}${libdir}/${BPN}
	
	# remove general purpose utilities
	rm -f ${D}${libdir}/bsearch ${D}${libdir}/idn-lookup \
		${D}${mandir}/man1/bsearch.1 ${D}${mandir}/man1/idn-lookup.1
	
	# remove unwanted files
	rm -f ${D}${libdir}/windc.so*
	rm -f ${D}${libdir}/windc.a
	
	# no translations for the moment
	rm -r ${D}${datadir}/locale
	
	# rewrite symlinks to symlinks to point directly to file
	#for l in ${D}${libdir}/${BPN}/*.a ${D}${libdir}/${BPN}/*.so; do
	#	ln ${BPN}/$l ${D}${libdir}/$l;
	#done
	mv ${D}${bindir}/krb5-config ${D}${bindir}/krb5-config.heimdal
	mv ${D}${mandir}/man1/krb5-config.1 ${D}${mandir}/man1/krb5-config.heimdal.1
	mkdir -p ${D}${sysconfdir}/default
	mkdir -p ${D}${sysconfdir}/ldap/schema
	install -m 0644 ${S}/debian/extras/default ${D}${sysconfdir}/default/heimdal-kdc
	install -m 0644 ${S}/lib/hdb/hdb.schema ${D}${sysconfdir}/ldap/schema/hdb.schema
	mv ${D}${bindir}/ftp ${D}${bindir}/kftp
	mv ${D}${mandir}/man1/ftp.1 ${D}${mandir}/man1/kftp.1
	mv ${D}${bindir}/pagsh ${D}${bindir}/kpagsh
	mv ${D}${bindir}/su ${D}${bindir}/ksu
	mv ${D}${mandir}/man1/pagsh.1 ${D}${mandir}/man1/kpagsh.1
	mv ${D}${mandir}/man1/su.1 ${D}${mandir}/man1/ksu.1
	mv ${D}${bindir}/kadmin ${D}${bindir}/kadmin.heimdal
	mv ${D}${mandir}/man1/kadmin.1 ${D}${mandir}/man1/kadmin.heimdal.1
	mv ${D}${bindir}/ktutil ${D}${bindir}/ktutil.heimdal
	mv ${D}${mandir}/man1/ktutil.1 ${D}${mandir}/man1/ktutil.heimdal.1
	mv ${D}${bindir}/kinit ${D}${bindir}/kinit.heimdal
	mv ${D}${bindir}/kswitch ${D}${bindir}/kswitch.heimdal
	mv ${D}${bindir}/kpasswd ${D}${bindir}/kpasswd.heimdal
	mv ${D}${bindir}/ksu ${D}${bindir}/ksu.heimdal
	mv ${D}${bindir}/kdestroy ${D}${bindir}/kdestroy.heimdal
	mv ${D}${bindir}/klist ${D}${bindir}/klist.heimdal
	mv ${D}${mandir}/man1/kinit.1 ${D}${mandir}/man1/kinit.heimdal.1
	mv ${D}${mandir}/man1/kdestroy.1 ${D}${mandir}/man1/kdestroy.heimdal.1
	mv ${D}${mandir}/man1/kpasswd.1 ${D}${mandir}/man1/kpasswd.heimdal.1
	mv ${D}${mandir}/man1/ksu.1 ${D}${mandir}/man1/ksu.heimdal.1
	mv ${D}${mandir}/man1/kswitch.1 ${D}${mandir}/man1/kswitch.heimdal.1
	mv ${D}${mandir}/man1/klist.1 ${D}${mandir}/man1/klist.heimdal.1
	mv ${D}${mandir}/man1/login.1 ${D}${mandir}/man1/login.heimdal.1
	mv ${D}${mandir}/man5/krb5.conf.5 ${D}${mandir}/man5/krb5.conf.5heimdal

	# remove conflicting files
	rm -rf debian/heimdal-dev/usr/include/ss
	rm -f debian/heimdal-dev/usr/bin/mk_cmds
	rm -f debian/heimdal-dev/usr/include/fnmatch.h
	# remove unwanted files
	rm -f debian/heimdal-dev/usr/lib/libss.a
	rm -f debian/heimdal-dev/usr/lib/libss.la
	rm -f debian/heimdal-dev/usr/lib/libss.so
	rm -f debian/heimdal-dev/usr/lib/windc.la
	# remove libtool recursive linking mess
	sed -i "/dependency_libs/ s/'.*'/''/" ${D}${libdir}/*.la
	sed -i "s/libdir=.*/libdir=\/usr\/lib\/heimdal/" \
		${D}${libdir}/pkgconfig/*.pc
	sed -i "s/includedir=.*/includedir=\/usr\/include\/heimdal/" \
		${D}${libdir}/pkgconfig/*.pc
	
	# Install init script
	install -d ${D}${sysconfdir}/init.d
	install -m 0644 ${S}/debian/heimdal-kcm.init \
			${D}${sysconfdir}/init.d/heimdal-kcm
	install -m 0644 ${S}/debian/heimdal-kdc.init \
			${D}${sysconfdir}/init.d/heimdal-kdc
	
	install -d ${D}${sysconfdir}/logrotate.d
	install -m 0644 ${S}/debian/heimdal-kdc.logrotate \
			${D}${sysconfdir}/logrotate.d/heimdal-kdc	
	
	# install heimdal-kdc package
	install -d ${D}${libdir}/${BPN}-servers
	mv ${D}${sbindir}/kadmind ${D}${libdir}/${BPN}-servers
	mv ${D}${sbindir}/kdc ${D}${libdir}/${BPN}-servers
	mv ${D}${sbindir}/kpasswdd ${D}${libdir}/${BPN}-servers
	
	# install heimdal-multidev package
	mv ${D}${sbindir}/${BPN}/asn1_compile ${D}${bindir}
	mv ${D}${sbindir}/${BPN}/asn1_print ${D}${bindir}
	mv ${D}${sbindir}/${BPN}/slc ${D}${bindir}

	# install heimdal-servers package
	install -d ${D}${libdir}/${BPN}-servers
	mv ${D}${bindir}/login ${D}${libdir}/${BPN}-servers
	mv ${D}${sbindir}/ftpd ${D}${libdir}/${BPN}-servers
	mv ${D}${sbindir}/kfd ${D}${libdir}/${BPN}-servers

	# install heimdal-servers-x package
	mv ${D}${sbindir}/kxd ${D}${libdir}/${BPN}-servers
}

PACKAGES =+ "${BPN}-servers-x ${BPN}-servers ${BPN}-multidev-static \
		${BPN}-clients-x ${BPN}-clients ${BPN}-kdc ${BPN}-kcm"

FILES_${BPN}-clients-x += "\
	${bindir}/kx ${bindir}/rxtelnet \
	${bindir}/rxterm ${bindir}/tenletxr ${bindir}/xnlock"
FILES_${BPN}-clients += "\
	${bindir}/* ${sbin}/kdigest \
	${sbin}/kimpersonate ${sbin}/push"
FILES_${BPN}-kcm += "\
	${sysconfdir}/init.d/heimdal-kcm \
	${sbindir}/kcm"
FILES_${BPN}-kdc += "\
	${datadir}/${BPN}-kdc/* \
	${sbindir}/digest-service ${sbindir}/hprop \
	${sbindir}/hpropd ${sbindir}/iprop-log \
	${sbindir}/ipropd-master ${sbindir}/ipropd-slave \
	${sbindir}/kstash ${libdir}/${BPN}-servers/kadmind \
	${libdir}/${BPN}-servers/kdc ${libdir}/kpasswdd \
	${sysconfdir}/ldap/schema/hdb.schema \
	${sysconfdir}/init.d/* ${sysconfdir}/default/* \
	${sysconfdir}/logrotate.d "
FILES_${BPN}-multidev += " \
	${bindir}/asn1* ${bindir}/slc \
	${bindir}/krb5-config.heimdal \
	${includedir}/${BPN}/* \
	${libdir}/${BPN}/*.so \
	${libdir}/pkgconfig/heimdal-gssapi.pc \
	${libdir}/pkgconfig/heimdal-kadm-client.pc \
	${libdir}/pkgconfig/heimdal-kadm-server.pc \
	${libdir}/pkgconfig/heimdal-krb5.pc \
	${libdir}/pkgconfig/kafs.pc "
FILES_${BPN}-servers += " \
	${libdir}/${BPN}-servers/*"
FILES_${BPN}-servers-x += " \
	${libdir}/${BPN}-servers/kxd"
FILES_${PN}-dbg += "${sbindir}/heimdal/.debug \
	${libdir}/${BPN}-servers/.debug"
BBCLASSEXTEND = "native nativesdk"

PARALLEL_MAKE = ""
