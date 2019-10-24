SUMMARY = "Sophisticated command-line FTP/HTTP/BitTorrent client programs"
DESCRIPTION = "Lftp is a file retrieving tool that supports FTP, HTTP, FISH, SFTP, HTTPS, \
FTPS and BitTorrent protocols under both IPv4 and IPv6. Lftp has an amazing \
set of features, while preserving its interface as simple and easy as possible."
HOMEPAGE = "https://lftp.tech"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit debian-package
require recipes-debian/sources/lftp.inc

inherit autotools gettext pkgconfig

PACKAGECONFIG ??= "gnutls libidn2 readline zlib"

PACKAGECONFIG[expat] = "--with-expat=${STAGING_INCDIR}/.., --without-expat, expat"
PACKAGECONFIG[gnutls] = "--with-gnutls, --without-gnutls, gnutls"
PACKAGECONFIG[libidn2] = "--with-libidn2, --without-libidn2, libidn2"
PACKAGECONFIG[openssl] = "--with-openssl, --without-openssl, openssl"
PACKAGECONFIG[readline] = "--with-readline=${STAGING_INCDIR}/.., --with-readline=no, readline"
PACKAGECONFIG[zlib] = "--with-zlib=${STAGING_INCDIR}/.., --without-zlib, zlib"

FILES_${PN} += "${datadir}/icons/hicolor"

RDEPENDS_${PN} += "perl bash"
