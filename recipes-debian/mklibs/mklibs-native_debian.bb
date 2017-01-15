inherit debian-package autotools gettext native pythonnative
PV = "0.1.40"

PR = "r0"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=98d31037b13d896e33890738ef01af64"

DEPENDS = "python-native dpkg-native libtimedate-perl-native"

#
# Debian Native Test
#
inherit debian-test

SRC_URI_DEBIAN_TEST = "\
        file://mklibs-native-test/run_native_test_mklibs \
        file://mklibs-native-test/run_version_command \
"

DEBIAN_NATIVE_TESTS = "run_native_test_mklibs"
TEST_DIR = "${B}/native-test"
