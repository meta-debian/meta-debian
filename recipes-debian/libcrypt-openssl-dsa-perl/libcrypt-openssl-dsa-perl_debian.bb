SUMMARY = "module which implements the DSA signature verification system"
DESCRIPTION = "\
    module which implements the DSA signature verification system 		\
    Crypt::OpenSSL::DSA is a wrapper to the DSA (Digital Signature Algorithm)	\
    functions contained in the OpenSSL crypto library. It allows you to create	\
    public/private key pair, sign messages and verify signatures, as well as	\
    manipulate the keys at the low level.					\
    "
HOMEPAGE = " http://perl-openssl.sourceforge.net/"

PR = "r0"
inherit debian-package

LICENSE = "Artistic-1.0 | GPL-1.0+"

LIC_FILES_CHKSUM = "file://debian/copyright;md5=0be6b49036a2378b24d9a7debd822b90"

#cpan is perl modules that use Makefile.PL to build system
inherit cpan

DEPENDS += "openssl"

#install follow debian jessie
do_install_append () {
	mv ${D}${libdir}/perl/vendor_perl/* ${D}${libdir}/perl/
	rm -r ${D}${libdir}/perl/vendor_perl
	mv ${D}${libdir}/perl/5.20.2 ${D}${libdir}/perl/5.20
	mv ${D}${libdir}/perl ${D}${libdir}/perl5
	chmod 0644 ${D}${libdir}/perl5/5.20/auto/Crypt/OpenSSL/DSA/DSA.so
}
FILES_${PN} = "${libdir}/*"
FILES_${PN}-dbg += "${libdir}/perl5/5.20/auto/Crypt/OpenSSL/DSA/.debug"
