#
# base recipe: meta/recipes-devtools/gcc/gcc-8.2.inc
# base branch: master
# base commit: da24071e92071ecbefe51314d82bf40f85172485
#

FILESPATH_append = "${FILE_DIRNAME}/gcc-8:${COREBASE}/meta/recipes-devtools/gcc/gcc-8.3:${COREBASE}/meta/recipes-devtools/gcc/gcc-8.3/backport"

require recipes-devtools/gcc/gcc-8.3.inc
BINV = "${PV}"

inherit debian-package
require recipes-debian/sources/gcc-8.inc
BPN = "gcc-8"
DEBIAN_UNPACK_DIR = "${TMPDIR}/work-shared/gcc-${PV}-${PR}/gcc-8-${PV}"
S = "${DEBIAN_UNPACK_DIR}/src"
DEBIAN_PATCH_TYPE = "quilt"

SRC_URI += " \
       file://0001-gcc-4.3.1-ARCH_FLAGS_FOR_TARGET.patch \
       file://0003-gcc-poison-system-directories.patch \
       file://0003-gcc-4.3.3-SYSROOT_CFLAGS_FOR_TARGET.patch \
       file://0004-64-bit-multilib-hack.patch \
       file://0007-optional-libstdc.patch \
       file://0006-gcc-disable-MASK_RELAX_PIC_CALLS-bit.patch \
       file://0007-COLLECT_GCC_OPTIONS.patch \
       file://0008-Use-the-defaults.h-in-B-instead-of-S-and-t-oe-in-B.patch \
       file://0009-fortran-cross-compile-hack.patch \
       file://0010-cpp-honor-sysroot.patch \
       file://0011-MIPS64-Default-to-N64-ABI.patch \
       file://0014-Define-GLIBC_DYNAMIC_LINKER-and-UCLIBC_DYNAMIC_LINKE.patch \
       file://0013-gcc-Fix-argument-list-too-long-error.patch \
       file://0014-Disable-sdt.patch \
       file://0015-libtool.patch \
       file://0016-gcc-armv4-pass-fix-v4bx-to-linker-to-support-EABI.patch \
       file://0017-Use-the-multilib-config-files-from-B-instead-of-usin.patch \
       file://0018-Avoid-using-libdir-from-.la-which-usually-points-to-.patch \
       file://0019-export-CPP.patch \
       file://0020-Disable-the-MULTILIB_OSDIRNAMES-and-other-multilib-o.patch \
       file://0021-Ensure-target-gcc-headers-can-be-included.patch \
       file://0022-gcc-4.8-won-t-build-with-disable-dependency-tracking.patch \
       file://0023-Don-t-search-host-directory-during-relink-if-inst_pr.patch \
       file://0024-Use-SYSTEMLIBS_DIR-replacement-instead-of-hardcoding.patch \
       file://0025-aarch64-Add-support-for-musl-ldso.patch \
       file://0026-libcc1-fix-libcc1-s-install-path-and-rpath.patch \
       file://0027-handle-sysroot-support-for-nativesdk-gcc.patch \
       file://0028-Search-target-sysroot-gcc-version-specific-dirs-with.patch \
       file://0031-Fix-various-_FOR_BUILD-and-related-variables.patch \
       file://0030-nios2-Define-MUSL_DYNAMIC_LINKER.patch \
       file://0033-Add-ssp_nonshared-to-link-commandline-for-musl-targe.patch \
       file://0032-libgcc-Add-knob-to-use-ldbl-128-on-ppc.patch \
       file://0033-Link-libgcc-using-LDFLAGS-not-just-SHLIB_LDFLAGS.patch \
       file://0034-libgcc_s-Use-alias-for-__cpu_indicator_init-instead-.patch \
       file://0035-sync-gcc-stddef.h-with-musl.patch \
       file://0036-fix-segmentation-fault-in-precompiled-header-generat.patch \
       file://0037-Fix-for-testsuite-failure.patch \
       file://0038-Re-introduce-spe-commandline-options.patch \
"

# CVE-2021-37322 is already fixed but NVD db is not updated.
CVE_CHECK_WHITELIST = "CVE-2021-37322"
