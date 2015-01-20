require recipes-kernel/kmod/kmod_git.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-kernel/kmod/kmod:\
${COREBASE}/meta/recipes-kernel/kmod/files:\
"

inherit debian-package
DEBIAN_SECTION = "admin"

DPR = "0"

LICENSE = "GPLv2 & LGPLv2.1"
LICENSE_libkmod = "LGPLv2.1"
LIC_FILES_CHKSUM = "\
 file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
 file://libkmod/COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
"

SRC_URI += "\
 file://depmod-search.conf \
 file://run-ptest \
 file://ptest.patch \
 file://avoid_parallel_tests.patch \
 file://fix-O_CLOEXEC.patch \
"
