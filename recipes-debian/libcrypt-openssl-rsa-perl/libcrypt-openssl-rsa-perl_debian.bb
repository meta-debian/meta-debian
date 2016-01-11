#
# base recipe: http://cgit.openembedded.org/meta-openembedded/tree/meta-perl/\
#		recipes-perl/libcrypt/libcrypt-openssl-rsa-perl_0.28.bb
# base branch: master
#

SUMMARY = " module for RSA encryption using OpenSSL"
DESCRIPTION = "\
    Crypt::OpenSSL::RSA is a Perl module that provides glue to the RSA functions\
    in the OpenSSL library. It provides the following functionalities: create \
    a key from a string, make a new key, save key to a string, save public \
    portion of key to a string using format compatible with OpenSSL's command-line\
    rsa tool, encrypt, decrypt, sign, verify, return the size in bytes of a key, \
    check the validity of a key"
HOMEPAGE = " http://perl-openssl.sourceforge.net/"

PR = "r0"
inherit debian-package

LICENSE = "Artistic-1.0 | GPL-1.0+"

LIC_FILES_CHKSUM = "file://LICENSE;md5=385c55653886acac3821999a3ccd17b3"

#cpan is perl modules that use Makefile.PL to build system
inherit cpan

DEPENDS += "openssl libcrypt-openssl-random-perl libcrypt-openssl-bignum-perl"
RDEPENDS_${PN} += "libcrypt-openssl-bignum-perl"

#install follow debian jessie
do_install_append () {
	mv ${D}${libdir}/perl/vendor_perl/* ${D}${libdir}/perl/
	rm -r ${D}${libdir}/perl/vendor_perl
	mv ${D}${libdir}/perl/5.20.2 ${D}${libdir}/perl/5.20
	mv ${D}${libdir}/perl ${D}${libdir}/perl5
	chmod 0644 ${D}${libdir}/perl5/5.20/auto/Crypt/OpenSSL/RSA/RSA.so
}
FILES_${PN} = "${libdir}/*"
FILES_${PN}-dbg += "${libdir}/perl5/5.20/auto/Crypt/OpenSSL/RSA/.debug"
