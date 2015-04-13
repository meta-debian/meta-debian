require recipes-devtools/elfutils/${BPN}_0.155.bb
FILESEXTRAPATHS_prepend = "\
${THISDIR}/files:${COREBASE}/meta/recipes-devtools/elfutils/elfutils-0.155:\
${COREBASE}/meta/recipes-devtools/elfutils/elfutils:\
"

inherit debian-package
DEBIAN_SECTION = "libs"
DPR = "0"

LICENSE = "GPL-2.0 & GPL-3.0 & LGPL-3.0"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
file://COPYING-GPLV2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
file://COPYING-LGPLV3;md5=e6a600fd5e1d9cbde2d983680233ad02 \
"

# Excluded inappropriate patches which for older version:
# redhat-portability.diff 
# redhat-robustify.diff
# hppa_backend.diff
# arm_backend.diff
# mips_backend.diff
# m68k_backend.diff
# nm-Fix-size-passed-to-snprintf-for-invalid-sh_name-case.patch
# elfutils-ar-c-fix-num-passed-to-memset.patch
# fix-build-gcc-4.8.patch

SRC_URI += "\
file://elf_additions_debian.diff \
file://mempcpy.patch \
file://dso-link-change.patch \
file://i386_dis.h \
file://x86_64_dis.h \
"

# For cross compiling
EXTRA_OECONF += " --host=${HOST_SYS} --with-biarch=yes"
