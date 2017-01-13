SUMMARY = "This package provides the GSSAPI plugin, compiled with the MIT Kerberos 5 library"

PR = "r0"
inherit debian-package
PV = "2.1.26.dfsg1"

LICENSE = "BSD-4-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=3f55e0974e3d6db00ca6f57f2d206396"
#avoid-to-call-AC_TRY_RUN.patch:
#       Avoid to call AC_TRY_RUN to check if GSSAPI libraries support SPNEGO
#       on cross-compile environment by definition AC_ARG_ENABLE enable-spnego
SRC_URI += "file://avoid-to-call-AC_TRY_RUN.patch \
	   "
inherit autotools-brokensep pkgconfig

DPN = "cyrus-sasl2"
DEPENDS = "openssl virtual/db krb5"
CPPFLAGS += " -I${STAGING_INCDIR}/mit-krb5"
LDFLAGS += " -L${STAGING_LIBDIR}/mit-krb5"

EXTRA_OECONF += "--with-dblib=berkeley 			\
		 --with-plugindir="${libdir}/sasl2" 	\
		 --with-bdb-incdir=${STAGING_INCDIR} 	\
		 --enable-gss_mutexes 			\
		 --with-gss_impl=mit 			\
		 --enable-gssapi=yes			\
		 --enable-cram=no                       \
		 --enable-plain=no                      \
		 --enable-otp=no                        \
		 --enable-digest=no                     \
		 --enable-anon=no                       \
		 andrew_cv_runpath_switch=none"

CFLAGS += "-fPIC"
PARALLEL_MAKEINST = ""

#runtime depends follow debian/control
RDEPENDS_${PN} += "libsasl2-modules"
RCONFLICTS_${PN} = "libsasl2-modules-gssapi-heimdal"
do_compile_prepend () {
	cd include
	${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS} makemd5.c -o makemd5
	touch makemd5.o makemd5.lo makemd5
	cd ..
}

do_install_append() {
	#These files provided by libsasl2 and cyrus-sasl2
	rm  ${D}${libdir}/*.so*
	rm -r \
		${D}${includedir} 			\
		${D}${libdir}/pkgconfig 		\
		${D}${sbindir} 				\
		${D}${datadir} 				
	rm ${D}${libdir}/sasl2/libsasldb.*
		
	#remove the unwanted files
	rm ${D}${libdir}/sasl2/*.la
	rm ${D}${libdir}/*.la
}
FILES_${PN}-dbg += "${libdir}/sasl2/.debug/*"
FILES_${PN} += "${libdir}/sasl2/*.so.*"
FILES_${PN}-dev += "${libdir}/sasl2/*.so"
