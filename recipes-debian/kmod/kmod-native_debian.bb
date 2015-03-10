SUMMARY = "Tools for managing Linux kernel modules"                             
DESCRIPTION = "kmod is a set of tools to handle common tasks with Linux kernel modules like \
               insert, remove, list, check properties, resolve dependencies and aliases."

HOMEPAGE = "http://packages.profusion.mobi/kmod/"

FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-kernel/kmod/kmod:"

inherit debian-package autotools ptest native debian-test
DEBIAN_SECTION = "admin"
PR = "r0"
DPR = "1"

DEPENDS += "pkgconfig-native"

LICENSE = "GPLv2 & LGPLv2.1"
LICENSE_libkmod = "LGPLv2.1"
LIC_FILES_CHKSUM = "\
 file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
 file://libkmod/COPYING;md5=a6f89e2100d9b6cdffcea4f398e37343 \
"

SRC_URI += "\
 file://depmod-search.conf \
 file://run-ptest \
 file://ptest.patch \
 file://avoid_parallel_tests.patch \
 file://fix-O_CLOEXEC.patch \
 file://Change-to-calling-bswap_-instead-of-htobe-and-be-toh_debian.patch \
"

EXTRA_AUTORECONF += "--install --symlink"
EXTRA_OECONF +="--enable-debug --enable-logging --enable-tools --disable-manpages\
		 --with-zlib"

do_configure_prepend () {
	touch ${S}/libkmod/docs/gtk-doc.make
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
