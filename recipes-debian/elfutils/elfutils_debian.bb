#
# base-recipe: meta/recipes-devtools/elfutils/elfutils_0.155.bb 
# base-branch: daisy
#

PR = "r0"

inherit debian-package autotools gettext
PV = "0.159"

LICENSE = "GPL-2.0 & GPL-3.0 & LGPL-3.0"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
file://COPYING-GPLV2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
file://COPYING-LGPLV3;md5=e6a600fd5e1d9cbde2d983680233ad02 \
"

DEPENDS = "libtool bzip2 zlib virtual/libintl"

# Only apply when building uclibc based target recipe
SRC_URI_append_libc-uclibc = " file://uclibc-support.patch"

# The buildsystem wants to generate 2 .h files from source using a binary it just built,
# which can not pass the cross compiling, so let's work around it by adding 2 .h files
# along with the do_configure_prepend()

SRC_URI += "\
        file://i386_dis.h \
        file://x86_64_dis.h \
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
"

EXTRA_OECONF = "--program-prefix=eu- --without-lzma"
EXTRA_OECONF_append_class-native = " --without-bzlib"
EXTRA_OECONF_append_libc-uclibc = " --enable-uclibc"

do_configure_prepend() {
	sed -i '/^i386_dis.h:/,+4 {/.*/d}' ${S}/libcpu/Makefile.am

	cp ${WORKDIR}/*dis.h ${S}/libcpu
}

do_install_append() {
	if [ "${TARGET_ARCH}" != "x86_64" ] && [ -z `echo "${TARGET_ARCH}"|grep 'i.86'` ];then
		rm ${D}${bindir}/eu-objdump
	fi
}

# we can not build complete elfutils when using uclibc
# but some recipes e.g. gcc 4.5 depends on libelf so we
# build only libelf for uclibc case

EXTRA_OEMAKE_libc-uclibc = "-C libelf"
EXTRA_OEMAKE_class-native = ""
EXTRA_OEMAKE_class-nativesdk = ""

BBCLASSEXTEND = "native nativesdk"

# Package utilities separately
PACKAGES =+ "libelf libasm libdw libdw-dev libasm-dev libelf-dev"

FILES_libelf = "${libdir}/libelf-*.so ${libdir}/libelf.so.*"
FILES_libasm = "${libdir}/libasm-*.so ${libdir}/libasm.so.*"
FILES_libdw  = "${libdir}/libdw-*.so ${libdir}/libdw.so.* ${libdir}/elfutils/lib*"
FILES_libelf-dev = "${libdir}/libelf.so ${includedir}"
FILES_libasm-dev = "${libdir}/libasm.so ${includedir}/elfutils/libasm.h"
FILES_libdw-dev  = "${libdir}/libdw.so  ${includedir}/dwarf.h ${includedir}/elfutils/libdw*.h"
# Some packages have the version preceeding the .so instead properly
# versioned .so.<version>, so we need to reorder and repackage.
#FILES_${PN} += "${libdir}/*-${PV}.so ${base_libdir}/*-${PV}.so"
#FILES_SOLIBSDEV = "${libdir}/libasm.so ${libdir}/libdw.so ${libdir}/libelf.so"

# The package contains symlinks that trip up insane
INSANE_SKIP_${MLPREFIX}libdw = "dev-so"

# For cross compiling
EXTRA_OECONF += " --host=${HOST_SYS} --with-biarch=yes"
