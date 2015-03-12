require recipes-extended/gperf/gperf_3.0.4.bb

inherit debian-package
DEBIAN_SECTION = "devel"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

#
# Debian Native Test
#
inherit debian-test

SRC_URI_DEBIAN_TEST = "\
        file://gperf-native-test/run_native_test_gperf \
        file://gperf-native-test/run_with_option-L \
        file://gperf-native-test/run_with_option-N \
        file://gperf-native-test/run_with_option-H \
        file://gperf-native-test/run_with_option-Z \
        file://gperf-native-test/command_line_options.gperf \
"

DEBIAN_NATIVE_TESTS = "run_native_test_gperf"
TEST_DIR = "${B}/native-test"
