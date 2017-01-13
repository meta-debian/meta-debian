#
# base recipe: meta/recipes-core/eglibc/ldconfig-native_2.12.1.bb
# base branch: daisy
#

SUMMARY = "A standalone native ldconfig build"

inherit debian-package
PV = "2.19"
PR = "r0"
DPN = "glibc"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "\
	file://${S}/elf/ldconfig.c;endline=17;md5=36f607e4dad4b434d452191621b2ce99\
"

# Patches *_debian.patch was created base on original patch in base recipe
# for correctly apply.
#
# For ldconfig-native build directly from glibc source code, these patches
# should be apply:
# Patch remove_xmalloc_header.patch replace xmalloc header in ldconfig.h
# by function declarations.
# Patch add_reportbug_info.patch add infor about bug reporting.
# Patch add_trusted_dirs_header.patch add trusted-dirs.h which was
# generated using awk in glibc source code.
SRC_URI += "\
	file://ldconfig_debian.patch\
	file://32and64bit_debian.patch\
	file://endian-ness_handling_debian.patch\
	file://flag_fix_debian.patch\
	file://endianess-header_debian.patch\
	file://ldconfig-default-to-all-multilib-dirs_debian.patch\
	file://remove_xmalloc_header.patch\
	file://add_reportbug_info.patch\
	file://add_trusted_dirs_header.patch\
"

FILESPATH = "${FILE_DIRNAME}/${PN}"

inherit native

# Override do_compile function from base recipe to build
# ldconfig from glibc source code.
do_compile () {
	$CC elf/ldconfig.c -std=gnu99 elf/chroot_canon.c locale/programs/xmalloc.c\ 
	    locale/programs/xstrdup.c elf/cache.c elf/readlib.c  elf/dl-cache.c\ 
		-o ldconfig
}

do_install () {
	install -d ${D}${bindir}
	install ${B}/ldconfig ${D}${bindir}/
}

#
# Debian Native Test
#
inherit debian-test

SRC_URI_DEBIAN_TEST = "\
	file://ldconfig-native-test/run_native_test_ldconfig \
	file://ldconfig-native-test/run_option-v \
	file://ldconfig-native-test/run_option-p \
"

DEBIAN_NATIVE_TESTS = "run_native_test_ldconfig"
TEST_DIR = "${B}/native-test"
