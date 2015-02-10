require recipes-support/libgcrypt/libgcrypt_1.5.3.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-support/libgcrypt/libgcrypt:\
${COREBASE}/meta/recipes-support/libgcrypt/files:\
"

inherit debian-package
DEBIAN_SECTION = "libs"

DPR = "0"
BPN = "libgcrypt11"

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f\
	file://COPYING.LIB;md5=bbb461211a33b134d42ed5ee802b37ff\
"

SRC_URI += "\
 file://add-pkgconfig-support.patch \
 file://libgcrypt-fix-building-error-with-O2-in-sysroot-path.patch \
"
