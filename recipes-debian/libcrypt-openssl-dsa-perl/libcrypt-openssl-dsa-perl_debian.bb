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
PV = "0.14"

LICENSE = "Artistic-1.0 | GPL-1.0+"

LIC_FILES_CHKSUM = "file://debian/copyright;md5=0be6b49036a2378b24d9a7debd822b90"

#cpan is perl modules that use Makefile.PL to build system
inherit cpan

DEPENDS += "openssl"

FILES_${PN} = "${libdir}/*"
