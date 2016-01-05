#
# base recipe: http://cgit.openembedded.org/meta-openembedded/tree/meta-perl/\
#		recipes-perl/libcrypt/libcrypt-openssl-random-perl_0.04.bb
# base branch: master
#

SUMMARY = "module to access the OpenSSL pseudo-random number generator"
HOMEPAGE = " http://perl-openssl.sourceforge.net/"

PR = "r0"
inherit debian-package

LICENSE = "Artistic-1.0 | GPL-1.0+"

LIC_FILES_CHKSUM = "file://LICENSE;md5=385c55653886acac3821999a3ccd17b3"

#cpan is perl modules that use Makefile.PL to build system
inherit cpan

DEPENDS += "openssl"

#install follow debian jessie
do_install_append () {
	mv ${D}${libdir}/perl/vendor_perl/* ${D}${libdir}/perl/
	rm -r ${D}${libdir}/perl/vendor_perl
	mv ${D}${libdir}/perl/5.20.2 ${D}${libdir}/perl/5.20
	mv ${D}${libdir}/perl ${D}${libdir}/perl5
	chmod 0644 ${D}${libdir}/perl5/5.20/auto/Crypt/OpenSSL/Random/Random.so
}
FILES_${PN} = "${libdir}/*"
FILES_${PN}-dbg += "${libdir}/perl5/5.20/auto/Crypt/OpenSSL/Random/.debug"
