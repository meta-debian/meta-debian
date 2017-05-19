#
# base recipe: http://cgit.openembedded.org/meta-openembedded/tree/
# meta-networking/recipes-daemons/cyrus-sasl/cyrus-sasl_2.1.26.bb
# base branch: master
#

SUMMARY = "Generic client/server library for SASL authentication"
DESCRIPTION = "This is the Cyrus SASL API implentation. It can be used on \
the client or server side to provide authentication and authorization services"

PR = "r0"
inherit debian-package
PV = "2.1.26.dfsg1"

DPN = "cyrus-sasl2"

DEPENDS = "openssl virtual/db"

LICENSE = "BSD-4-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=3f55e0974e3d6db00ca6f57f2d206396"

inherit autotools-brokensep pkgconfig

EXTRA_OECONF += "--with-dblib=berkeley 			\
                 --with-plugindir="${libdir}/sasl2" 	\
		 --with-bdb-incdir=${STAGING_INCDIR} 	\
		 --enable-ntlm 				\
		 --enable-static 			\
		 --disable-gssapi 			\
                 andrew_cv_runpath_switch=none"

PACKAGECONFIG ??= "ntlm ldap \
        ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)} \
        "
PACKAGECONFIG[pam] = "--with-pam,--without-pam,libpam,"
PACKAGECONFIG[opie] = "--with-opie,--without-opie,opie,"
PACKAGECONFIG[des] = "--with-des,--without-des,,"
PACKAGECONFIG[ntlm] = "--with-ntlm,--without-ntlm,,"

CFLAGS += "-fPIC"
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

do_install_append () {
	#Remove these file provided by cyrus-sasl2
	rm -r ${D}${libdir}/sasl2
	rm -r ${D}${sbindir}

	#Correct the permission of files follow Debian jessie
	LINKLIB=$(basename $(readlink ${D}${libdir}/libsasl2.so))
	chmod 0644 ${D}${libdir}/${LINKLIB}

	#remove unwanted files
	rm ${D}${libdir}/*.la
}

FILES_${PN}-dbg		+= "${libdir}/sasl2/.debug/* \
			${bindir}/.debug/sasl-sample-client"

FILES_${PN}-dev         += "${libdir}/sasl2/*.so \
			${libdir}/pkgconfig"
# Correct .deb file name
DEBIANNAME_${PN}-dev                            = "libsasl2-dev"
BBCLASSEXTEND = "native"
