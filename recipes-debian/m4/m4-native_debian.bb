require m4.inc

PR = "r0"

inherit native

INHIBIT_AUTOTOOLS_DEPS = "1"
DEPENDS += "gnu-config-native"

# Change timestamp of some files to avoid remaking them (which require
# automake to be installed)
# 'make -d' command gives clue on this 
do_configure_prepend() {
	# To avoid running aclocal
	touch ${S}/aclocal.m4

	# To avoid running automake
	touch ${S}/Makefile.in

	# To avoid rebuilding manpages.
	touch ${S}/doc/m4.1

	# To avoid running autoconf
	touch ${S}/configure

	# To avoid running autoheader   
	touch ${S}/lib/config.hin
}

do_configure()  {
        install -m 0644 ${STAGING_DATADIR}/gnu-config/config.sub .
        install -m 0644 ${STAGING_DATADIR}/gnu-config/config.guess .
        oe_runconf
}

do_compile_prepend() {
	[ -d ${STAGING_INCDIR_NATIVE} ] || mkdir -p ${STAGING_INCDIR_NATIVE}
}

#
# Debian Native Test
#
inherit debian-test

SRC_URI_DEBIAN_TEST = "\
        file://m4-native-test/run_native_test_m4 \
	file://m4-native-test/run_file_without_options \
	file://m4-native-test/run_file_with_options-E \
	file://m4-native-test/run_file_with_options-i \
	file://m4-native-test/run_file_with_options-P \
	file://m4-native-test/run_file_with_options-Q \
	file://m4-native-test/run_help_command \
	file://m4-native-test/run_version_command \
	file://m4-native-test/example.m4 \
"

DEBIAN_NATIVE_TESTS = "run_native_test_m4"
TEST_DIR = "${B}/native-test"
