#
# base recipe: meta/recipes-support/gmp/gmp.inc
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "6.0.0+dfsg"

SUMMARY = "GNU multiprecision arithmetic library"
DESCRIPTION = "GMP is a free library for arbitrary precision arithmetic, \
operating on signed integers, rational numbers, and floating point numbers"
LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit autotools

ARM_INSTRUCTION_SET = "arm"

acpaths = ""

BBCLASSEXTEND = "native nativesdk"

EXTRA_OECONF += " --enable-cxx=detect"

PACKAGES =+ "libgmpxx"
FILES_libgmpxx = "${libdir}/libgmpxx${SOLIBS}"

SSTATE_SCAN_FILES += "gmp.h"

#
# configure.patch: Not use from reused recipe since version is different.
# Patch file was created for new version with the same purpose.
# 
# amd64.patch: Patch file from reused recipe.
#
SRC_URI += "\
file://configure.patch \
file://amd64.patch \
"
