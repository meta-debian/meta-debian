#
# Base recipe: meta/recipes-devtools/quilt/quilt-native_0.61.bb
# Base branch: daisy
#

require quilt.inc

PR = "${INC_PR}.0"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

RDEPENDS_${PN} = "diffstat-native patch-native bzip2-native util-linux-native"

INHIBIT_AUTOTOOLS_DEPS = "1"

inherit native

PATCHTOOL = "patch"

EXTRA_OECONF = "--disable-nls"

do_configure () {
	oe_runconf
}

# Install package follow Debian
do_install_append () {
	# Dummy quiltrc file for patch.bbclass
	install -d ${D}${sysconfdir}/
	touch ${D}${sysconfdir}/quiltrc

	install -m 0755 ${S}/debian/deb3 ${D}${bindir}
	install -m 0755 ${S}/debian/dh_quilt_patch ${D}${bindir}
	install -m 0755 ${S}/debian/dh_quilt_unpatch ${D}${bindir}
}

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
	file://quilt-native-test/run_help_command \
	file://quilt-native-test/run_version_command \
	file://quilt-native-test/run_trace_command \
	file://quilt-native-test/run_series_command \
	file://quilt-native-test/run_top_command \
	file://quilt-native-test/run_pop_command \
	file://quilt-native-test/run_import_command \
	file://quilt-native-test/run_push_command \
	file://quilt-native-test/03_test \
	file://quilt-native-test/add.c \
"

DEBIAN_NATIVE_TESTS = "run_native_test_quilt"
TEST_DIR = "${B}/quilt-native-test"
