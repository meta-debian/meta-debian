#
# base recipe: http://cgit.openembedded.org/meta-openembedded/tree/
# meta-networking/recipes-daemons/cyrus-sasl/cyrus-sasl_2.1.26.bb
# base branch: master
# base commit: 33468a1a620c1bfbc3e0d5fa3ecc05fd88cf655a
#

SUMMARY = "Generic client/server library for SASL authentication"
DESCRIPTION = "This is the Cyrus SASL API implentation. It can be used on \
the client or server side to provide authentication and authorization services"

PR = "r0"
inherit debian-package
PV = "2.1.26.dfsg1"

DEPENDS = "openssl virtual/db"
#DEPENDS = "openssl virtual/db heimdal"
LICENSE = "BSD-4-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=3f55e0974e3d6db00ca6f57f2d206396"

#avoid-to-call-AC_TRY_RUN.patch:
#	Avoid to call AC_TRY_RUN to check if GSSAPI libraries support SPNEGO
#	on cross-compile environment by definition AC_ARG_ENABLE enable-spnego
#SRC_URI += "file://avoid-to-call-AC_TRY_RUN.patch \
#	   "
inherit autotools-brokensep pkgconfig

EXTRA_OECONF += "\
	--with-dblib=berkeley 			\
	--with-plugindir="${libdir}/sasl2" 	\
	--with-bdb-incdir=${STAGING_INCDIR} 	\
	--enable-sql 				\
	--enable-otp 				\
	--enable-gss_mutexes 			\
	--enable-login 				\
	--disable-gssapi 			\
	--enable-scram=no 			\
	--enable-ntlm 				\
	andrew_cv_runpath_switch=none"

PACKAGECONFIG ??= "ntlm ldap \
	${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)} \
        "
PACKAGECONFIG[pam]  = "--with-pam,--without-pam,libpam,"
PACKAGECONFIG[opie] = "--with-opie,--without-opie,opie,"
PACKAGECONFIG[des]  = "--with-des,--without-des,,"
PACKAGECONFIG[ldap] = "--with-ldap=${STAGING_LIBDIR} --enable-ldapdb,\
		       --without-ldap --disable-ldapdb,libldap,"
PACKAGECONFIG[ntlm] = "--with-ntlm,--without-ntlm,,"

CFLAGS += "-fPIC"
PARALLEL_MAKE = ""
PARALLEL_MAKEINST = ""

#runtime depends follow debian/control
RDEPENDS_${PN}-bin = "libsasl2"
RDEPENDS_${PN}-libsasl2-2 += "libsasl2-modules-db"
RDEPENDS_libsasl2-modules-ldap += "libsasl2-modules"
RDEPENDS_libsasl2-modules-otp += "libsasl2-modules"
RDEPENDS_libsasl2-modules-sql += "libsasl2-modules"

do_compile_prepend () {
	cd include
	${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS} makemd5.c -o makemd5
	touch makemd5.o makemd5.lo makemd5
	cd ..
}

do_compile_append () {
	oe_runmake 'SAS_INC=-I${S}/include -L${S}/lib/.libs -lsasl2' \
		-C ${S}/sample sample-server sample-client	
}

do_install_append() {
	install -d ${D}${bindir}
	install -d ${D}${sysconfdir}/default
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${sysconfdir}/logcheck/ignore.d.server

	mv ${D}${sbindir}/dbconverter-2 ${D}${sbindir}/sasldbconverter2
	mv ${D}${sbindir}/pluginviewer ${D}${sbindir}/saslpluginviewer
	
	# Install sample-server;sample client;gen-auth; saslfinger;saslauthd;logcheck server
	install -m 0755 ${S}/sample/sample-server ${D}${sbindir}/sasl-sample-server
	install -m 0755 ${S}/sample/sample-client ${D}${bindir}/sasl-sample-client
	install -m 0755 ${S}/debian/gen-auth/gen-auth ${D}${bindir}/gen-auth
	install -m 0755 ${S}/debian/saslfinger/saslfinger 		\
			${D}${bindir}/saslfinger
	install -m 0644 ${S}/debian/sasl2-bin.saslauthd.default 	\
			${D}${sysconfdir}/default/saslauthd
	install -m 0755 ${S}/debian/sasl2-bin.saslauthd.init 	\
			${D}${sysconfdir}/init.d/saslauthd
	install -m 0755 ${S}/debian/libsasl2-modules.logcheck.server \
			${D}${sysconfdir}/logcheck/ignore.d.server/libsasl2-modules

	#Correct the permission of files follow Debian jessie
	for f in ${D}${libdir}/sasl2/*.so; do
		LINKLIB=$(basename $(readlink $f))
		chmod 0644 ${D}${libdir}/sasl2/${LINKLIB}
	done

	#These files provided by libsasl2 and libsasl2-modules-gssapi*
	rm  ${D}${libdir}/*.so*
	rm -r ${D}${includedir}
	rm -r ${D}${libdir}/pkgconfig
	
	#remove the unwanted files
	rm ${D}${libdir}/sasl2/*.la ${D}${libdir}/*.la
}

PACKAGES =+ "${PN}-bin libsasl2-modules libsasl2-modules-db 	\
		libsasl2-modules-sql libsasl2-modules-otp 	\
		libsasl2-modules-ldap "

FILES_${PN}-bin	+= "${bindir}/* 				\
		${systemd_unitdir}/system/saslauthd.service 	\
		${sysconfdir}/tmpfiles.d/saslauthd 		\
		${sbindir}/sasl-sample-server 			\
		$sbindir}/sasl-sample-server 			\
		${bindir}/sasl-sample-client 			\
		${bindir}/gen-auth 				\
		${bindir}/saslfinger 				\
		${sysconfdir}/default/saslauthd 		\
		${sysconfdir}/init.d/saslauthd"

FILES_${PN}-dev	+= "${libdir}/sasl2/*.so"

FILES_${PN}-dbg	+= "${libdir}/sasl2/.debug 			\
		${bindir}/.debug/sasl-sample-client"

FILES_libsasl2-modules += "${libdir}/sasl2/libanonymous.so.* 	\
			${libdir}/sasl2/libcrammd5.so.* 	\
			${libdir}/sasl2/libdigestmd5.so.* 	\
			${libdir}/sasl2/liblogin.so.* 		\
			${libdir}/sasl2/libntlm.so.* 		\
			${libdir}/sasl2/libplain.so.* 		\
			${sysconfdir}/logcheck/ignore.d.server"

FILES_libsasl2-modules-db += 	"${libdir}/sasl2/libsasldb.so.*"

FILES_libsasl2-modules-sql += 	"${libdir}/sasl2/libsql.so.*"

FILES_libsasl2-modules-otp += 	"${libdir}/sasl2/libotp.so.*"

FILES_libsasl2-modules-ldap += 	"${libdir}/sasl2/libldapdb.so.*"

# Correct the packages name
DEBIANNAME_${PN} 				= "libsasl2-2"
DEBIANNAME_${PN}-dev 				= "libsasl2-dev"
DEBIANNAME_${PN}-dbg 				= "cyrus-sasl2-dbg"
DEBIANNAME_${PN}-doc 				= "cyrus-sasl2-doc"
DEBIANNAME_${PN}-staticdev 			= "cyrus-sasl2-staticdev"
DEBIANNAME_${PN}-libsasl2-modules 		= "libsasl2-modules"
DEBIANNAME_${PN}-libsasl2-modules-db    	= "libsasl2-modules-db"
DEBIANNAME_${PN}-libsasl2-2   			= "libsasl2-2"
DEBIANNAME_${PN}-libsasl2-modules-sql 		= "libsasl2-modules-sql"
PKG_${PN}-bin 					= "sasl2-bin"
DEBIANNAME_${PN}-libsasl2-modules-otp 		= "libsasl2-modules-otp"
DEBIANNAME_${PN}-libsasl2-modules-ldap   	= "libsasl2-modules-ldap"
