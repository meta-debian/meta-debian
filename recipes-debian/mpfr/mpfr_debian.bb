#
# Base recipe: meta/recipes-support/mpfr/mpfr_3.1.2.bb
# Base branch: daisy
# Base commit: 9e4aad97c3b4395edeb9dc44bfad1092cdf30a47
#
SUMMARY = "C library for multiple-precision floating-point computations with exact rounding"
HOMEPAGE = "http://www.mpfr.org/"
LICENSE = "GPLv3+ & LGPLv3+"

PR = "r0"
DPN = "mpfr4"
DEPENDS = "gmp"

inherit autotools debian-package
PV = "3.1.2"

LIC_FILES_CHKSUM = " \
file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
file://COPYING.LESSER;md5=6a6a8e020838b23406c81b19c1d46df6 \
"

SRC_URI += " \
file://long-long-thumb.patch \
"

# In Debian, binary package name of mpfr is "lib${PN}"
DEBIANNAME_${PN}-dbg = "lib${DPN}-dbg"
DEBIANNAME_${PN}-dev = "lib${PN}-dev"
DEBIANNAME_${PN}-doc = "lib${PN}-doc"
DEBIANNAME_${PN} = "lib${DPN}"

BBCLASSEXTEND = "native nativesdk"
