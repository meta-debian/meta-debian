require apache2.inc

PR = "${INC_PR}.1"

DEPENDS = "expat-native pcre-native apr-native apr-util-native"

inherit native

SRC_URI += " \
    file://httpd-2.4.3-fix-race-issue-of-dir-install.patch \
"

EXTRA_OECONF += " \
    --with-apr=${STAGING_BINDIR_CROSS}/apr-1-config \
    --with-apr-util=${STAGING_BINDIR_CROSS}/apu-1-config \
"

do_install_append() {
	install -d ${D}${bindir}
	install -m 0755 ${B}/server/gen_test_char ${D}${bindir}
	local shebang_length=`head  -n 1 ${D}/${bindir}/apxs | wc -m`
	if [ ${shebang_length} -gt 127 ]; then
		sed -i -e "s|${OECMAKE_PERLNATIVE_DIR}/perl -w|/usr/bin/env perl|" ${D}/${bindir}/apxs
	fi
}
