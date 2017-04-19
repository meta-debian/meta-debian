#
# Base recipe: meta/recipes-extended/pigz/pigz_2.3.1.bb
# Base branch: daisy
# Base commit: 9e4aad97c3b4395edeb9dc44bfad1092cdf30a47
#

SUMMARY = "A parallel implementation of gzip"
DESCRIPTION = "pigz, which stands for parallel implementation of gzip, is a \
fully functional replacement for gzip that exploits multiple processors and \
multiple cores to the hilt when compressing data. pigz was written by Mark \
Adler, and uses the zlib and pthread libraries."
HOMEPAGE = "http://zlib.net/pigz/"

PR = "0"
DEPENDS = "zlib"

EXTRA_OEMAKE = "-e MAKEFLAGS="

inherit debian-package
PV = "2.3.1"

LICENSE = "Zlib"
LIC_FILES_CHKSUM = " \
file://README;md5=d9835b8537721e63621b30c67e1af3e3 \
file://pigz.c;beginline=7;endline=21;md5=a21d4075cb00ab4ca17fce5e7534ca95 \
"

PROVIDES_class-native += "gzip-native"

do_install () {
        if [ "${CLASSOVERRIDE}" = "class-target" ] ; then
                # Install files into /bin (FHS), which is typical place for gzip
                install -d ${D}${base_bindir}
                install ${B}/pigz ${D}${base_bindir}/gzip
                install ${B}/unpigz ${D}${base_bindir}/gunzip
        else
                install -d ${D}${bindir}
                install ${B}/pigz ${D}${bindir}/gzip
                install ${B}/unpigz ${D}${bindir}/gunzip
        fi
}

ALTERNATIVE_${PN} = "gzip gunzip"
ALTERNATIVE_LINK_NAME[gzip] = "${base_bindir}/gzip"
ALTERNATIVE_LINK_NAME[gunzip] = "${base_bindir}/gunzip"
ALTERNATIVE_PRIORITY = "80"

NATIVE_PACKAGE_PATH_SUFFIX = "/${PN}"

BBCLASSEXTEND = "native nativesdk"

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
