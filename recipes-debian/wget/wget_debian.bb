SUMMARY = "Console URL download utility supporting HTTP, FTP, etc"
HOMEPAGE = "https://www.gnu.org/software/wget/"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

PR = "r1"
inherit debian-package
PV = "1.16"

DEPENDS = "gnutls zlib libidn"

inherit autotools gettext

RRECOMMENDS_${PN} += "ca-certificates"

# Follow debian/rules
EXTRA_OECONF = "--enable-ipv6 --with-ssl --with-libidn=${STAGING_LIBDIR}/../ \
                --disable-rpath"

BBCLASSEXTEND += "nativesdk"
