SUMMARY = "daemon for NSS and PAM lookups using LDAP"
DESCIPTION = "This package provides a daemon for retrieving user accounts and \
similar system information from LDAP. It is used by the libnss-ldapd and \
libpam-ldapd packages but is not very useful by itself."
HOMEPAGE = "http://arthurdejong.org/nss-pam-ldapd/"

PR = "r0"

inherit debian-package

DEPENDS += "libpam openldap cyrus-sasl2 krb5"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

EXTRA_OECONF += "--libdir=${base_libdir} \
		--with-pam-seclib-dir=${base_libdir}/security \
		--enable-warnings \
		--enable-pynslcd"

inherit autotools

PV = "0.9.4"

do_install_append(){
	install -d ${D}${datadir}/lintian/overrides
	install -m 0644 ${S}/debian/libnss-ldapd.lintian-overrides ${D}${datadir}/lintian/overrides/libnss-ldapd
	install -m 0644 ${S}/debian/pynslcd.lintian-overrides ${D}${datadir}/lintian/overrides/pynslcd

	install -d ${D}${datadir}/pam-configs
	install -m 0644 ${S}/debian/pam-configs/ldap ${D}${datadir}/pam-configs/ldap

	install -d ${D}${sysconfdir}/default
	install -m 0644 ${S}/debian/nslcd.default ${D}${sysconfdir}/default/nslcd
	install -m 0644 ${S}/debian/nslcd.default ${D}${sysconfdir}/default/pynslcd

	install -d ${D}${sysconfdir}/init.d
	install -m 0644 ${S}/debian/nslcd.init ${D}${sysconfdir}/init.d/nslcd
	sed 's/^\(# Provides: *\|NSLCD_NAME=\)nslcd/\1pynslcd/' ${S}/debian/nslcd.init > ${S}/debian/pynslcd.init
	install -m 0644 ${S}/debian/pynslcd.init ${D}${sysconfdir}/init.d/pynslcd

	install -d ${D}${sysconfdir}/network/if-up.d
	install -m 0644 ${S}/debian/nslcd.if-up ${D}${sysconfdir}/network/if-up.d/nslcd
}

PACKAGES =+ "nslcd nslcd-utils pynslcd libpam-ldapd libnss-ldapd"

FILES_nslcd = "${sbindir}/nslcd \
	${sysconfdir}/init.d/nslcd \
	${sysconfdir}/network/if-up.d/nslcd \
	${sysconfdir}/default/nslcd"
FILES_nslcd-utils = "${bindir}/chsh.ldap \ 
	${bindir}/getent.ldap \
	${datadir}/nslcd-utils/* \
	${datadir}/python/runtime.d/nslcd-utils.rtupdate"
FILES_pynslcd = "${sysconfdir}/default/pynslcd \
	${sysconfdir}/init.d/pynslcd \
	${sbindir}/pynslcd \
	${datadir}/lintian/overrides/pynslcd \
	${datadir}/pynslcd/* \
	${datadir}/python/runtime.d/pynslcd.rtupdate"
FILES_libpam-ldapd = "${base_libdir}/security/pam_ldap.so \
	${datadir}/pam-configs/ldap"
FILES_libnss-ldapd = "${datadir}/lintian/overrides/libnss-ldapd \
        ${base_libdir}/libnss_ldap.so.*"
FILES_${PN}-dbg += "${base_libdir}/security/.debug/* \
	${base_libdir}/alpha-linux-gnu/.debug/*"
