require recipes-devtools/quilt/${PN}_0.61.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-devtools/quilt/quilt:"

inherit debian-package
DEBIAN_SECTION = "vcs"

DPR = "0"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI += " \
file://install.patch \
file://run-ptest \
file://Makefile \
"

# quilt-native also depends on native quilt command.
# This is a special overwritten to apply all patches in
# the quilt source without native quilt command.
debian_patch_quilt() {
	bbnote "applying all patches without quilt command"

	PATCH_DIR=${DEBIAN_UNPACK_DIR}/debian/patches
	if [ ! -s ${PATCH_DIR}/series ]; then
		bbfatal "no patch in series"
	fi
	for patch in $(sed "s@#.*@@" ${PATCH_DIR}/series); do
		bbnote "applying $patch"
		patch -p1 < ${PATCH_DIR}/${patch}
	done
}

#
# Debian Native Test
#
inherit debian-test

SRC_URI_DEBIAN_TEST = "\
        file://quilt-native-test/run_native_test_quilt \
	file://quilt-native-test/run_without_options \
	file://quilt-native-test/run_version_command \
	file://quilt-native-test/run_trace_command \
	file://quilt-native-test/run_series_command \
	file://quilt-native-test/run_top_command \
	file://quilt-native-test/run_add_command \
"

DEBIAN_NATIVE_TESTS = "run_native_test_quilt"
TEST_DIR = "${B}/native-test"
