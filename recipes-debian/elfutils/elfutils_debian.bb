# base recipe: meta/recipes-devtools/elfutils/elfutils_0.176.bb
# base branch: master
# base commit: 17bfcbacb0fa6e4641919fd92bcb86c5bb86c761

SUMMARY = "Utilities and libraries for handling compiled object files"
HOMEPAGE = "https://sourceware.org/elfutils"
SECTION = "base"
LICENSE = "GPLv3+ & GPLv2 & LGPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://COPYING-GPLV2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING-LGPLV3;md5=e6a600fd5e1d9cbde2d983680233ad02 \
                    "
DEPENDS = "libtool bzip2 zlib virtual/libintl"
DEPENDS_append_libc-musl = " argp-standalone fts musl-obstack "
# The Debian patches below are from:
# http://ftp.de.debian.org/debian/pool/main/e/elfutils/elfutils_0.176-1.1.debian.tar.xz
inherit debian-package
require recipes-debian/sources/elfutils.inc
FILESPATH_append = ":${COREBASE}/meta/recipes-devtools/elfutils/files:${THISDIR}/files"

SRC_URI += " \
           file://0001-dso-link-change.patch \
           file://0002-Fix-elf_cvt_gunhash-if-dest-and-src-are-same.patch \
           file://0003-fixheadercheck.patch \
           file://0004-Disable-the-test-to-convert-euc-jp.patch \
           file://0006-Fix-build-on-aarch64-musl.patch \
           file://0001-libasm-may-link-with-libbz2-if-found.patch \
           file://0001-libelf-elf_end.c-check-data_list.data.d.d_buf-before.patch \
           file://0001-skip-the-test-when-gcc-not-deployed.patch \
           file://run-ptest \
           file://ptest.patch \
           "

SRC_URI_append_libc-musl = " \
           file://musl-obstack-fts.patch \
           file://musl-libs.patch \
           file://musl-utils.patch \
           file://musl-tests.patch \
           "

inherit autotools gettext ptest

EXTRA_OECONF = "--program-prefix=eu- --without-lzma"
EXTRA_OECONF_append_class-native = " --without-bzlib"
RDEPENDS_${PN}-ptest += "libasm libelf bash make coreutils ${PN}-binutils"

EXTRA_OECONF_append_class-target += "--disable-tests-rpath"

RDEPENDS_${PN}-ptest_append_libc-glibc = " glibc-utils"

do_compile_ptest() {
       cd ${B}/tests
       oe_runmake buildtest-TESTS oecheck
}

do_install_ptest() {
       if [ ${PTEST_ENABLED} = "1" ]; then
               # copy the files which needed by the cases
               TEST_FILES="strip strip.o addr2line elfcmp objdump readelf size.o nm.o nm elflint"
               install -d -m 755                       ${D}${PTEST_PATH}/src
               install -d -m 755                       ${D}${PTEST_PATH}/libelf
               install -d -m 755                       ${D}${PTEST_PATH}/libdw
               for test_file in ${TEST_FILES}; do
                       if [ -f ${B}/src/${test_file} ]; then
                               cp -r ${B}/src/${test_file} ${D}${PTEST_PATH}/src
                       fi
               done
               cp ${D}${libdir}/libelf-${PV}.so ${D}${PTEST_PATH}/libelf/libelf.so
               cp ${D}${libdir}/libdw-${PV}.so ${D}${PTEST_PATH}/libdw/libdw.so
               cp -r ${S}/tests/                       ${D}${PTEST_PATH}
               cp -r ${B}/tests/*                      ${D}${PTEST_PATH}/tests
               cp -r ${B}/config.h                     ${D}${PTEST_PATH}
               cp -r ${B}/backends                     ${D}${PTEST_PATH}
               sed -i '/^Makefile:/c Makefile:'        ${D}${PTEST_PATH}/tests/Makefile
               find ${D}${PTEST_PATH} -type f -name *.[hoc] | xargs -i rm {}
       fi
}

EXTRA_OEMAKE_class-native = ""
EXTRA_OEMAKE_class-nativesdk = ""

BBCLASSEXTEND = "native nativesdk"

# Package utilities separately
PACKAGES =+ "${PN}-binutils libelf libasm libdw"

# shared libraries are licensed GPLv2 or GPLv3+, binaries GPLv3+
# according to NEWS file:
# "The license is now GPLv2/LGPLv3+ for the libraries and GPLv3+ for stand-alone
# programs. There is now also a formal CONTRIBUTING document describing how to
# submit patches."
LICENSE_${PN}-binutils = "GPLv3+"
LICENSE_${PN} = "GPLv3+"
LICENSE_libelf = "GPLv2 | LGPLv3+"
LICENSE_libasm = "GPLv2 | LGPLv3+"
LICENSE_libdw = "GPLv2 | LGPLv3+"

FILES_${PN}-binutils = "\
    ${bindir}/eu-addr2line \
    ${bindir}/eu-ld \
    ${bindir}/eu-nm \
    ${bindir}/eu-readelf \
    ${bindir}/eu-size \
    ${bindir}/eu-strip"

FILES_libelf = "${libdir}/libelf-${PV}.so ${libdir}/libelf.so.*"
FILES_libasm = "${libdir}/libasm-${PV}.so ${libdir}/libasm.so.*"
FILES_libdw  = "${libdir}/libdw-${PV}.so ${libdir}/libdw.so.* ${libdir}/elfutils/lib*"
# Some packages have the version preceeding the .so instead properly
# versioned .so.<version>, so we need to reorder and repackage.
#FILES_${PN} += "${libdir}/*-${PV}.so ${base_libdir}/*-${PV}.so"
#FILES_SOLIBSDEV = "${libdir}/libasm.so ${libdir}/libdw.so ${libdir}/libelf.so"

# The package contains symlinks that trip up insane
INSANE_SKIP_${MLPREFIX}libdw = "dev-so"

# avoid stripping some generated binaries otherwise some of the tests such as test-nlist,
# run-strip-reloc.sh, run-strip-strmerge.sh and so on will fail
INHIBIT_PACKAGE_STRIP_FILES = "\
    ${PKGD}${PTEST_PATH}/tests/test-nlist \
    ${PKGD}${PTEST_PATH}/tests/elfstrmerge \
    ${PKGD}${PTEST_PATH}/tests/backtrace-child \
    ${PKGD}${PTEST_PATH}/tests/backtrace-data \
    ${PKGD}${PTEST_PATH}/tests/backtrace-dwarf \
    ${PKGD}${PTEST_PATH}/tests/deleted \
    ${PKGD}${PTEST_PATH}/src/strip \
    ${PKGD}${PTEST_PATH}/src/addr2line \
    ${PKGD}${PTEST_PATH}/src/elfcmp \
    ${PKGD}${PTEST_PATH}/src/objdump \
    ${PKGD}${PTEST_PATH}/src/readelf \
    ${PKGD}${PTEST_PATH}/src/nm \
    ${PKGD}${PTEST_PATH}/src/elflint \
    ${PKGD}${PTEST_PATH}/libelf/libelf.so \
    ${PKGD}${PTEST_PATH}/libdw/libdw.so \
    ${PKGD}${PTEST_PATH}/backends/libebl_i386.so \
    ${PKGD}${PTEST_PATH}/backends/libebl_x86_64.so \
"

PRIVATE_LIBS_${PN}-ptest = "libdw.so.1 libelf.so.1"
