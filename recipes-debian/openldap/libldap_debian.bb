PR = "r0"
inherit debian-package

LICENSE = "OLDAP-2.8"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=f2bdbaa4f50199a00b6de2ca7ec1db05 \
                    file://LICENSE;md5=153d07ef052c4a37a8fac23bc6031972 \
"

LDAP_VER = "${@'.'.join(d.getVar('PV',1).split('.')[0:2])}"

DPN = "openldap"

# Makefile_debian.patch:
#       This patch is correct the path of $LIBTOOL, $CC, $LDAP_LIB, $LDAP_INC  
#       in the Makefiles (including the Makefiles in subfolders)
SRC_URI += "file://Makefile_debian.patch"

DEPENDS = "util-linux groff-native"

#Declare Debian patch type
DEBIAN_PATCH_TYPE = "quilt"

inherit autotools-brokensep update-rc.d systemd

# CV SETTINGS
# Required to work round AC_FUNC_MEMCMP which gets the wrong answer
# when cross compiling (should be in site?)
EXTRA_OECONF += "ac_cv_func_memcmp_working=yes"

# CONFIG DEFINITIONS
# The following is necessary because it cannot be determined for a
# cross compile automagically.  Select should yield fine on all OE
# systems...
EXTRA_OECONF += "--with-yielding-select=yes"
# Shared libraries are nice...
EXTRA_OECONF += "--enable-dynamic"

PACKAGECONFIG ??= "gnutls modules sasl \
"
#--with-tls              with TLS/SSL support auto|openssl|gnutls [auto]
PACKAGECONFIG[gnutls] = "--with-tls=gnutls,,gnutls libgcrypt"

PACKAGECONFIG[sasl] = "--with-cyrus-sasl,--without-cyrus-sasl,libsasl2"
PACKAGECONFIG[modules] = "lt_cv_dlopen_self=yes --enable-modules,--disable-modules,libtool"

# SLAPD options
#
# UNIX crypt(3) passwd support:
EXTRA_OECONF += "--enable-crypt"

# Append URANDOM_DEVICE='/dev/urandom' to CPPFLAGS:
# This allows tls to obtain random bits from /dev/urandom, by default
# it was disabled for cross-compiling.
CPPFLAGS_append = " -D_GNU_SOURCE -DURANDOM_DEVICE=\'/dev/urandom\'"

do_configure_prepend() {
	sed -i -e "s:##STAGING_INCDIR##:${STAGING_INCDIR}:g" ${S}/contrib/slapd-modules/lastbind/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/lastbind/Makefile
	sed -i -e "s:##STAGING_INCDIR##:${STAGING_INCDIR}:g" ${S}/contrib/slapd-modules/smbk5pwd/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/smbk5pwd/Makefile
	sed -i -e "s:##STAGING_INCDIR##:${STAGING_INCDIR}:g" ${S}/contrib/slapd-modules/autogroup/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/autogroup/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/acl/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/dupent/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/passwd/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/passwd/sha2/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/passwd/pbkdf2/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/kinit/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/allowed/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/denyop/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/dsaschema/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/trace/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/nssov/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/samba4/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/noopsrch/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/addpartial/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/smbk5pwd/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/allop/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/autogroup/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/proxyOld/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/comp_match/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/lastmod/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/cloak/Makefile
	sed -i -e "s:##TARGET_SYS##:${TARGET_SYS}:g" ${S}/contrib/slapd-modules/nops/Makefile
}
do_configure() {
	cp ${STAGING_DATADIR_NATIVE}/libtool/config/ltmain.sh ${S}/build
	aclocal
	libtoolize --force --copy
	gnu-configize
	autoconf
	oe_runconf
}

do_install_append () {
	#remove the conflict files with openldap
	#rm -r ${D}${sysconfdir}
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

PACKAGES =+ "libldap-2.4-2"
PKG_${PN}-dev = "libldap2-minimal-dev"

FILES_libldap-2.4-2 = "${libdir}/liblber*.so.* ${libdir}/libldap*.so.* ${sysconfdir}/ldap/ldap.conf"

BBCLASSEXTEND = "native"
