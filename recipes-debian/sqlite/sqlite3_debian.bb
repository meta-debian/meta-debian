require recipes-support/sqlite/sqlite3_3.8.3.1.bb

inherit debian-package
DEBIAN_SECTION = "devel"
DPR = "0"

LICENSE = "PD"
LIC_FILES_CHKSUM = " \
  file://src/sqlite.h.in;endline=11;md5=65f0a57ca6928710b418c094b3570bb0 \
"

DEPENDS += "tcl-native"
do_compile[depends] += "tcl-native:do_populate_sysroot"

# Required to avoid a compile error
SRC_URI += "file://fix-hardcode-libtool.patch"

do_compile_prepend_class-target () {
	export CROSS_BUILDING="yes"
}

do_install_prepend_class-target () {
	export CROSS_BUILDING="yes"
}

# Fix error: ./lemon: Command not found
# lemon need be built with $(BCC) instead of $(LTLINK)
do_compile_prepend_class-nativesdk(){
	export CROSS_BUILDING="yes"
}

do_install_prepend_class-nativesdk(){
	export CROSS_BUILDING="yes"
}

#
# Debian Native Test
#
inherit debian-test

SRC_URI_DEBIAN_TEST = "\
        file://sqlite3-native-test/run_native_test_sqlite3 \
        file://sqlite3-native-test/run_create_table \
        file://sqlite3-native-test/run_insert_record \
        file://sqlite3-native-test/run_view_record \
        file://sqlite3-native-test/run_alter_tablename \
        file://sqlite3-native-test/run_version_command \
        file://sqlite3-native-test/run_drop_table \
        file://sqlite3-native-test/insert-data.sql \
"

DEBIAN_NATIVE_TESTS = "run_native_test_sqlite3"
TEST_DIR = "${B}/native-test"
