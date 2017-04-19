#
# base recipe: https://github.com/openembedded/openembedded/blob/master/\
# recipes/perl/libcrypt-openssl-bignum-perl_0.04.bb
#

SUMMARY = "\
    Perl module to access OpenSSL multiprecision integer arithmetic libraries"
DESCRIPTION = "\
    Presently, many though not all of the arithmetic operations that OpenSSL	\
    provides are exposed to Perl via Crypt::OpenSSL::Bignum. In addition, this	\
    module can be used to provide access to bignum values produced by other	\
    OpenSSL modules, such as key parameters from Crypt::OpenSSL::RSA. 		\
    "
HOMEPAGE = " http://perl-openssl.sourceforge.net/"

PR = "r0"
inherit debian-package
PV = "0.04"

LICENSE = "Artistic-1.0 | GPL-1.0+"

LIC_FILES_CHKSUM = "file://LICENSE;md5=385c55653886acac3821999a3ccd17b3"

#cpan is perl modules that use Makefile.PL to build system
inherit cpan

# Source package has no patch.
DEBIAN_QUILT_PATCHES = ""

DEPENDS += "openssl"

FILES_${PN} = "${libdir}/*"
