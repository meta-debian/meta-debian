SUMMARY = "Console URL download utility supporting HTTP, FTP, etc"
HOMEPAGE = "https://www.gnu.org/software/wget/"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

PR = "r0"
inherit debian-package

DEPENDS = "gnutls zlib libpcre"

inherit autotools gettext

RRECOMMENDS_${PN} += "ca-certificates"

# Follow debian/rules
EXTRA_OECONF = "--enable-ipv6 --with-ssl --with-libidn=${STAGING_LIBDIR}/../"

BBCLASSEXTEND += "nativesdk"
