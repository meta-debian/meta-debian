require recipes-extended/pigz/pigz_2.3.1.bb

inherit debian-package

DEBIAN_SECTION = "utils"
DPR = "0"

LICENSE = "Zlib"
LIC_FILES_CHKSUM = " \
file://README;md5=d9835b8537721e63621b30c67e1af3e3 \
file://pigz.c;beginline=7;endline=21;md5=a21d4075cb00ab4ca17fce5e7534ca95 \
"

#
# Debian Native Test
#
inherit debian-test

SRC_URI_DEBIAN_TEST = "\
        file://pigz-native-test/run_native_test_pigz \
	file://pigz-native-test/run_version_command \
	file://pigz-native-test/run_help_command \
	file://pigz-native-test/run_gzip_command \
	file://pigz-native-test/run_gunzip_command \
	file://pigz-native-test/compress.gz.test \
"

DEBIAN_NATIVE_TESTS = "run_native_test_pigz"
TEST_DIR = "${B}/native-test"
