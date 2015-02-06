# Make sure that the following line is already in
# conf/local.conf or meta-debian/conf/layer.conf:
# PREFERRED_VERSION_binutils-cross = "debian"

require recipes-devtools/binutils/${PN}_2.24.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-devtools/binutils/binutils:"

DPN = "binutils"

inherit debian-package
DEBIAN_SECTION = "devel"
DPR = "0"

LICENSE = "GPLv2 & LGPLv2 & GPLv3 & LGPLv3"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552 \
file://COPYING3;md5=d32239bcb673463ab874e80d47fae504 \
file://COPYING.LIB;md5=9f604d8a4f8e74f4f5140845a21b6674 \
file://COPYING3.LIB;md5=6a6a8e020838b23406c81b19c1d46df6 \
"

# alway try to apply debian patches by quilt
DEBIAN_PATCH_TYPE = "quilt"

# Exclude following patches because they are already applied
# binutils-uclibc-300-001_ld_makefile_patch.patch
# binutils-uclibc-300-012_check_ldrunpath_length.patch
# fix-pr15815.patch
# fix-pr2404.patch
# fix-pr16476.patch
# fix-pr16428.patch
# replace_macros_with_static_inline.patch
# 0001-Fix-MMIX-build-breakage-from-bfd_set_section_vma-cha.patch
# binutils-uninitialised-warning.patch
# 
# Exclude mips64-default-ld-emulation.patch because it implements
# for old version
SRC_URI += " \
file://binutils-uclibc-100-uclibc-conf.patch \
file://binutils-uclibc-300-006_better_file_error.patch \
file://binutils-uclibc-gas-needs-libm.patch \
file://libtool-2.4-update_debian.patch \
file://libiberty_path_fix.patch \
file://binutils-poison_debian.patch \
file://libtool-rpath-fix_debian.patch \
file://binutils-armv5e.patch \
file://binutils-xlp-support_debian.patch \
"
