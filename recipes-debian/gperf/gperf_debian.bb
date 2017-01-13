#
# base recipe: meta/recipes-extended/gperf/gperf_3.0.4.bb
# branch: daisy
#

PR = "r0"

inherit debian-package
PV = "3.0.4"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

# defined() must not be used for array and hash
# this causes fatal errors from perl 5.22
SRC_URI += "file://texi2html-dont-use-defined-for-arrays.patch"

inherit autotools

# autoreconf couldn't find acinclude.m4 when stepping into subdirectory. Instead of
# duplicating acinclude.m4 in every subdirectory, use absolute include path to aclocal
EXTRA_AUTORECONF += " -I ${S}"

do_configure_prepend() {
        if [ ! -e ${S}/acinclude.m4 ]; then
                cat ${S}/aclocal.m4 > ${S}/acinclude.m4
        fi
}

BBCLASSEXTEND = "native"

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
