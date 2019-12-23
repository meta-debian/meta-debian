SUMMARY = "GNU Privacy Guard - encryption and signing tools (2.x)"
HOMEPAGE = "https://www.gnupg.org/"

LICENSE = "GPLv3+ & LGPLv3+ & LGPLv2.1+ & MIT & BSD & CC0-1.0"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=189af8afca6d6075ba6c9e0aa8077626 \
    file://COPYING.LGPL3;md5=a2b6bf2cb38ee52619e60f30a1fc7257 \
    file://COPYING.LGPL21;md5=3c9636424f4ef15d6cb24f934190cfb0 \
    file://dirmngr/dns.c;endline=24;md5=d98c3fe089fa9f62c11e1a288f7648df \
    file://tests/gpgscm/LICENSE.TinySCHEME;md5=56767db68d6d79f35ef5b3030c0bc9cd \
    file://COPYING.CC0;md5=5364f88a4fb7a4d2d24c350fa08ddbad \
"

inherit debian-package
require recipes-debian/sources/gnupg2.inc

FILESPATH_append = ":${COREBASE}/meta/recipes-support/gnupg/gnupg"
SRC_URI += " \
    file://0001-Use-pkg-config-to-find-pth-instead-of-pth-config.patch \
    file://0002-use-pkgconfig-instead-of-npth-config.patch \
"

SRC_URI_append_class-native = " \
    file://0001-configure.ac-use-a-custom-value-for-the-location-of-.patch \
    file://relocate.patch \
"

DEPENDS = "npth libksba libassuan libgcrypt bzip2 readline zlib"

inherit autotools gettext pkgconfig texinfo

EXTRA_OECONF = " \
    --disable-ldap \
    --disable-ccid-driver \
    --with-zlib=${STAGING_LIBDIR}/.. \
    --with-bzip2=${STAGING_LIBDIR}/.. \
    --with-readline=${STAGING_LIBDIR}/.. \
    --enable-gpg-is-gpg2 \
"

PACKAGECONFIG ??= "gnutls"
PACKAGECONFIG[gnutls] = "--enable-gnutls, --disable-gnutls, gnutls"
PACKAGECONFIG[sqlite3] = "--enable-sqlite, --disable-sqlite, sqlite3"

do_configure_prepend() {
	rm -f ${S}/m4/gpg-error.m4
	rm -f ${S}/m4/libassuan.m4
	rm -f ${S}/m4/ksba.m4
	rm -f ${S}/m4/libgcrypt.m4
}

do_install_append() {
	ln -sf gpg2 ${D}${bindir}/gpg
	ln -sf gpgv2 ${D}${bindir}/gpgv
}

do_install_append_class-native() {
	create_wrapper ${D}${bindir}/gpg2 GNUPG_BINDIR=${STAGING_BINDIR_NATIVE}
}

RRECOMMENDS_${PN} = "pinentry"

BBCLASSEXTEND = "native"
