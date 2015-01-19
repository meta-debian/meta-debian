require recipes-support/mpfr/mpfr_3.1.2.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-support/mpfr/mpfr-3.1.2:"

BPN = "mpfr4"
inherit debian-package

DEBIAN_SECTION = "math"
DPR = "0"

LICENSE = "GPLv3+ & LGPLv3+"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
file://COPYING.LESSER;md5=6a6a8e020838b23406c81b19c1d46df6 \
"

SRC_URI += " \
file://long-long-thumb.patch \
"
