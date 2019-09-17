#
# base recipe: meta/recipes-connectivity/socat/socat_1.7.3.2.bb
# base branch: warrior
#

SUMMARY = "Multipurpose relay for bidirectional data transfer"
DESCRIPTION = "Socat is a relay for bidirectional data \
transfer between two independent data channels."
HOMEPAGE = "http://www.dest-unreach.org/socat/"

DEPENDS = "openssl readline"

LICENSE = "GPL-2.0-with-OpenSSL-exception"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://README;beginline=257;endline=287;md5=338c05eadd013872abb1d6e198e10a3f"

inherit debian-package
require recipes-debian/sources/socat.inc

FILESPATH_append = ":${COREBASE}/meta/recipes-connectivity/socat/socat"
SRC_URI += "file://Makefile.in-fix-for-parallel-build.patch"

inherit autotools

EXTRA_AUTORECONF += "--exclude=autoheader"

EXTRA_OECONF += "ac_cv_have_z_modifier=yes \
                 ac_cv_header_bsd_libutil_h=no \
                 sc_cv_termios_ispeed=no \
                 ${TERMBITS_SHIFTS} \
"

TERMBITS_SHIFTS ?= "sc_cv_sys_crdly_shift=9 \
                    sc_cv_sys_tabdly_shift=11 \
                    sc_cv_sys_csize_shift=4"

TERMBITS_SHIFTS_powerpc = "sc_cv_sys_crdly_shift=12 \
                           sc_cv_sys_tabdly_shift=10 \
                           sc_cv_sys_csize_shift=8"

TERMBITS_SHIFTS_powerpc64 = "sc_cv_sys_crdly_shift=12 \
                             sc_cv_sys_tabdly_shift=10 \
                             sc_cv_sys_csize_shift=8"

PACKAGECONFIG_class-target ??= "tcp-wrappers"
PACKAGECONFIG ??= ""
PACKAGECONFIG[tcp-wrappers] = "--enable-libwrap,--disable-libwrap,tcp-wrappers"

do_install_prepend () {
	mkdir -p ${D}${bindir}
	install -d ${D}${bindir} ${D}${mandir}/man1
}

BBCLASSEXTEND = "native nativesdk"
