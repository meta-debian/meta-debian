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
DEBIAN_UNPACK_DIR = "${WORKDIR}/mpfr-${@d.getVar('PV', True).replace('~','-')}"

LIC_FILES_CHKSUM = " \
file://COPYING;md5=1ebbd3e34237af26da5dc08a4e440464 \
file://COPYING.LESSER;md5=3000208d539ec061b899bce1d9ce9404 \
"

inherit autotools
DEPENDS += "gmp autoconf-archive"

BBCLASSEXTEND = "native nativesdk"
