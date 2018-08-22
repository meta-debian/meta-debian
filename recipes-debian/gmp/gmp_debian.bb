#
# base recipe: meta/recipes-support/gmp/gmp_6.1.2.bb
# base branch: master
# base commit: d886fa118c930d0e551f2a0ed02b35d08617f746
#
require recipes-support/gmp/gmp.inc

inherit debian-package
require recipes-debian/sources/gmp.inc
DEBIAN_UNPACK_DIR = "${WORKDIR}/${BPN}-${REPACK_PV}"

LICENSE = "GPLv2+ | LGPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                   file://COPYING.LESSERv3;md5=6a6a8e020838b23406c81b19c1d46df6 \
                   file://COPYINGv2;md5=b234ee4d69f5fce4486a80fdaf4a4263"

REVISION = ""
SRC_URI += "file://amd64.patch \
            file://use-includedir.patch \
            file://0001-Append-the-user-provided-flags-to-the-auto-detected-.patch \
            file://0001-confiure.ac-Believe-the-cflags-from-environment.patch \
           "
FILESEXTRAPATHS =. "${COREBASE}/meta/recipes-support/gmp/gmp-6.1.2:"
acpaths = ""

EXTRA_OECONF += " --enable-cxx=detect"
EXTRA_OECONF_mipsarchr6_append = " --disable-assembly"

PACKAGES =+ "libgmpxx"
FILES_libgmpxx = "${libdir}/libgmpxx${SOLIBS}"

do_install_prepend_class-target() {
	sed -i \
	    -e "s|--sysroot=${STAGING_DIR_HOST}||g" \
	    -e "s|${DEBUG_PREFIX_MAP}||g" \
	    ${B}/gmp.h
}

SSTATE_SCAN_FILES += "gmp.h"

# Doesn't compile in MIPS16e mode due to use of hand-written
# assembly
MIPS_INSTRUCTION_SET = "mips"

BBCLASSEXTEND = "native nativesdk"
