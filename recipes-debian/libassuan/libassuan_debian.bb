#
# base recipe: meta/recipes-support/libassuan/libassuan_2.5.3.bb
# base branch: warrior
SUMMARY = "IPC library used by GnuPG and GPGME"
HOMEPAGE = "http://www.gnupg.org/related_software/libassuan/"

LICENSE = "GPLv3+ & LGPLv2.1+"
LICENSE_${PN} = "LGPLv2.1+"
LICENSE_${PN}-doc = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949 \
                    file://COPYING.LIB;md5=2d5025d4aa3495befef8f17206a5b0a1 \
                    file://src/assuan.c;endline=20;md5=ab92143a5a2adabd06d7994d1467ea5c\
                    file://src/assuan-defs.h;endline=20;md5=15d950c83e82978e35b35e790d7e4d39"

inherit debian-package
require recipes-debian/sources/libassuan.inc

DEPENDS = "libgpg-error"
FILESEXTRAPATHS =. "${COREBASE}/meta/recipes-support/libassuan/libassuan:"

SRC_URI += "file://libassuan-add-pkgconfig-support.patch"

BINCONFIG = "${bindir}/libassuan-config"

inherit autotools texinfo binconfig-disabled pkgconfig

do_configure_prepend () {
	# Else these could be used in preference to those in aclocal-copy
	rm -f ${S}/m4/*.m4
}

BBCLASSEXTEND = "native nativesdk"
