#
# base recipe: meta/recipes-kernel/kmod/kmod-native_git.bb
# base branch: daisy
#

require kmod.inc

PR = "${INC_PR}.0"

inherit native debian-test

SRC_URI += "\
	file://Change-to-calling-bswap_-instead-of-htobe-and-be-toh_debian.patch \
"

do_install_append(){
	for tool in depmod insmod lsmod modinfo modprobe rmmod; do
		ln -sf kmod ${D}${base_bindir}/${tool}
	done
}

#
# Debian Native Test
#

SRC_URI_DEBIAN_TEST =" \
 file://native_testcases/run_native_test_kmod \
 file://native_testcases/run_version_command \
 file://native_testcases/run_help_command \
 file://native_testcases/run_list_command \
 file://native_testcases/run_static_nodes_command \
"

DEBIAN_NATIVE_TESTS = "run_native_test_kmod"
TEST_DIR = "${B}/test"
