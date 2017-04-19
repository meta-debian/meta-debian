#
# base recipe: http://cgit.openembedded.org/meta-openembedded/tree/meta-perl/\
#		recipes-perl/libcrypt/libcrypt-openssl-random-perl_0.04.bb
# base branch: master
#

SUMMARY = "module to access the OpenSSL pseudo-random number generator"
HOMEPAGE = " http://perl-openssl.sourceforge.net/"

PR = "r0"
inherit debian-package
PV = "0.04"

LICENSE = "Artistic-1.0 | GPL-1.0+"

LIC_FILES_CHKSUM = "file://LICENSE;md5=385c55653886acac3821999a3ccd17b3"

#cpan is perl modules that use Makefile.PL to build system
inherit cpan

DEPENDS += "openssl"

FILES_${PN} = "${libdir}/*"
