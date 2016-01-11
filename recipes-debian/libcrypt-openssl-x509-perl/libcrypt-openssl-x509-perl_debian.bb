SUMMARY = "Perl extension to OpenSSL's X509 API"
DESCRIPTION = "\
	The Crypt::OpenSSL::X509 module implements a large majority \
	of OpenSSL's useful X509 API \
	"
HOMEPAGE = "https://metacpan.org/release/Crypt-OpenSSL-X509/"

PR = "r0"
inherit debian-package

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=20f729223c405227d6d39279518db307"

#
# Makefile-correct-INC_debian.patch:
#	This patch correct the path to perl library and header files
#
SRC_URI += "file://Makefile-correct-INC_debian.patch"

#cpan is perl modules that use Makefile.PL to build system
inherit cpan

DEPENDS += "openssl"

#include perl headers
CFLAGS =+ " -I${STAGING_LIBDIR}/perl/5.20.2/CORE/ "

do_configure_prepend() {
	sed -i -e "s:##STAGING_INCDIR##:${STAGING_INCDIR}:g" ${S}/Makefile.PL
	sed -i -e "s:##STAGING_LIBDIR##:${STAGING_LIBDIR}:g" ${S}/Makefile.PL
}

#install follow debian jessie
do_install_append () {
	mv ${D}${libdir}/perl/vendor_perl/* ${D}${libdir}/perl/
	rm -r ${D}${libdir}/perl/vendor_perl
	mv ${D}${libdir}/perl/5.20.2 ${D}${libdir}/perl/5.20
	mv ${D}${libdir}/perl ${D}${libdir}/perl5
	chmod 0644 ${D}${libdir}/perl5/5.20/auto/Crypt/OpenSSL/X509/X509.so
}
FILES_${PN} = "${libdir}/*"
FILES_${PN}-dbg += "${libdir}/perl5/5.20/auto/Crypt/OpenSSL/X509/.debug"
