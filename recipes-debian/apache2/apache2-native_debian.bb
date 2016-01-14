# This recipe is only for providing gen_test_char command
# to run while build apache2 target.

PR = "r0"

inherit debian-package

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=dbff5a2b542fa58854455bf1a0b94b83"

DEPENDS = "expat-native pcre-native apr-native apr-util-native"

inherit autotools pkgconfig native

# ap_cv_void_ptr_lt_long=no:
# 	Avoid error: Size of "void *" is less than size of "long"
EXTRA_OECONF = " \
    --with-apr=${STAGING_BINDIR_CROSS}/apr-1-config \
    --with-apr-util=${STAGING_BINDIR_CROSS}/apu-1-config \
    ap_cv_void_ptr_lt_long=no \
"

do_install() {
	install -d ${D}${bindir}
	install -m 0755 ${B}/server/gen_test_char ${D}${bindir}
}
