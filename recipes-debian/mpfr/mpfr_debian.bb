#
# Base recipe: meta/recipes-support/mpfr/mpfr_3.1.5.bb
# Base branch: master
# Base commit: d886fa118c930d0e551f2a0ed02b35d08617f746
#
SUMMARY = "C library for multiple-precision floating-point computations with exact rounding"
HOMEPAGE = "http://www.mpfr.org/"
LICENSE = "GPLv3+ & LGPLv3+"

inherit debian-package
require recipes-debian/sources/mpfr4.inc
BPN = "mpfr4"
DEBIAN_UNPACK_DIR = "${WORKDIR}/mpfr-${PV}"

LIC_FILES_CHKSUM = " \
file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
file://COPYING.LESSER;md5=6a6a8e020838b23406c81b19c1d46df6 \
"

inherit autotools
DEPENDS += "gmp autoconf-archive"

BBCLASSEXTEND = "native nativesdk"
