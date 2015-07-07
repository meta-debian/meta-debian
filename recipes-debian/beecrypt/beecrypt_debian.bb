#
# base recipe: meta/recipes-support/beecrypt/beecrypt_4.2.1.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=9894370afd5dfe7d02b8d14319e729a1 \
file://COPYING.LIB;md5=dcf3c825659e82539645da41a7908589 \
"
SRC_URI += " \
file://run-ptest \
file://beecrypt-enable-ptest-support.patch \
"

SRC_URI[md5sum] = "8441c014170823f2dff97e33df55af1e"
SRC_URI[sha256sum] = "286f1f56080d1a6b1d024003a5fa2158f4ff82cae0c6829d3c476a4b5898c55d"

inherit autotools multilib_header ptest
acpaths=""

do_install_append() {
        oe_multilib_header beecrypt/gnu.h
}

EXTRA_OECONF = "--without-python --enable-static --without-java"

PACKAGECONFIG ??= ""
PACKAGECONFIG[cplusplus] = "--with-cplusplus,--without-cplusplus,icu"

FILES_${PN} = "${sysconfdir} ${libdir}/*.so.* ${libdir}/${BPN}/*.so.*"
FILES_${PN}-dev += "${libdir}/${BPN}/*.so ${libdir}/${BPN}/*.la"
FILES_${PN}-staticdev += "${libdir}/${BPN}/*.a"

BBCLASSEXTEND = "native nativesdk"

do_install_ptest () {
        mkdir ${D}${PTEST_PATH}/tests
        cp -r ${B}/tests/.libs/test* ${D}${PTEST_PATH}/tests
}
