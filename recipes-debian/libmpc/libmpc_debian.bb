#
# Base recipe: meta/recipes-support/libmpc/libmpc_1.0.2.bb
# Base branch: daisy
# Base commit: 9e4aad97c3b4395edeb9dc44bfad1092cdf30a47
#

SUMMARY = "C library for complex number arithmetic with arbitrary precision and correct rounding"
DESCRIPTION = "Mpc is a C library for the arithmetic of complex numbers with arbitrarily high precision and correct rounding of the result. It is built upon and follows the same principles as Mpfr"
HOMEPAGE = "http://www.multiprecision.org/"
LICENSE = "LGPLv3"
SECTION = "libs"

inherit autotools debian-package pkgconfig
PV = "1.0.2"
PR = "r0"
DEPENDS = "gmp mpfr"
DPN = "mpclib3"

LICENSE = "LGPLv3"
LIC_FILES_CHKSUM = "file://COPYING.LESSER;md5=e6a600fd5e1d9cbde2d983680233ad02"

DEBIAN_PATCH_TYPE = "nopatch"

# Remove -Werror when initialize automake 
SRC_URI += "\
	file://fix-configure.patch \
"

BBCLASSEXTEND = "native nativesdk"
