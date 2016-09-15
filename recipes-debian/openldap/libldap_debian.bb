require openldap.inc
PR = "${INC_PR}.0"

DPN = "openldap"

# Makefile_debian.patch:
#       This patch is correct the path of $LIBTOOL, $CC, $LDAP_LIB, $LDAP_INC  
#       in the Makefiles (including the Makefiles in subfolders)
# install-strip.patch:
#       The original top.mk used INSTALL, not INSTALL_STRIP_PROGRAM when
#       installing .so and executables, this fails in cross compilation
#       environments
SRC_URI += "\
	file://Makefile_debian.patch \
	file://install-strip.patch"

PACKAGECONFIG ??= "gnutls modules sasl \
"
#--with-tls              with TLS/SSL support auto|openssl|gnutls [auto]
PACKAGECONFIG[gnutls] = "--with-tls=gnutls,,gnutls libgcrypt"

PACKAGECONFIG[sasl] = "--with-cyrus-sasl,--without-cyrus-sasl,libsasl2"
PACKAGECONFIG[modules] = "lt_cv_dlopen_self=yes --enable-modules,--disable-modules,libtool"

do_install_append () {
	#remove the conflict files with openldap
	mv ${D}${sysconfdir}/openldap ${D}${sysconfdir}/ldap
	rm -rf ${D}${sysconfdir}/ldap/schema
	rm ${D}${sysconfdir}/ldap/ldap.conf.* ${D}${sysconfdir}/ldap/slapd.* \
		${D}${sysconfdir}/ldap/DB_CONFIG.example ${D}${includedir}/slapi-plugin.h
	rm -r ${D}${libdir}/*.a ${D}${libdir}/*.la 
	rm -r ${D}${libdir}/libldap

	LINKLIB=$(basename $(readlink ${D}${libdir}/libldap-2.4.so.2))

	rm ${D}${libdir}/$LINKLIB ${D}${libdir}/libldap-2.4.so.2
	ln -s libldap_r-2.4.so.2 ${D}${libdir}/libldap-2.4.so.2
	rm ${D}${libdir}/libldap.so
	ln -s libldap_r.so ${D}${libdir}/libldap.so
	rm -r ${D}${bindir}
	rm -r ${D}${sbindir}
	
	rm -r ${D}${datadir}
	rm -r ${D}${localstatedir}
}

PKG_${PN} = "${PN}-2.4-2"

BBCLASSEXTEND = "native"
