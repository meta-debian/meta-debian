require recipes-core/eglibc/ldconfig-native_2.12.1.bb

inherit debian-package
DEBIAN_SECTION = "libs"
DPR = "0"
DPN = "glibc"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "\
	file://${S}/elf/ldconfig.c;endline=17;md5=36f607e4dad4b434d452191621b2ce99\
"

FILESPATH_prepend = "\
${COREBASE}/meta/recipes-core/eglibc/ldconfig-native-2.12.1:\
${THISDIR}/files:\
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
	file://endian-ness_handling_edited.patch\
	file://flag_fix_debian.patch\
	file://endianess-header_debian.patch\
	file://ldconfig-default-to-all-multilib-dirs_debian.patch\
	file://remove_xmalloc_header.patch\
	file://add_reportbug_info.patch\
	file://add_trusted_dirs_header.patch\
"

# Override do_compile function from base recipe to build
# ldconfig from glibc source code.
do_compile () {
	$CC elf/ldconfig.c -std=gnu99 elf/chroot_canon.c locale/programs/xmalloc.c\ 
	    locale/programs/xstrdup.c elf/cache.c elf/readlib.c  elf/dl-cache.c\ 
		-o ldconfig
}
