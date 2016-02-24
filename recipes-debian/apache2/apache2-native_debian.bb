require apache2.inc

PR = "${INC_PR}.1"

DEPENDS = "expat-native pcre-native apr-native apr-util-native"

inherit native

EXTRA_OECONF += " \
    --with-apr=${STAGING_BINDIR_CROSS}/apr-1-config \
    --with-apr-util=${STAGING_BINDIR_CROSS}/apu-1-config \
"

do_install_append() {
	install -d ${D}${bindir}
	install -m 0755 ${B}/server/gen_test_char ${D}${bindir}
}
