SUMMARY = "A portable foreign function interface library"
HOMEPAGE = "http://sourceware.org/libffi/"
DESCRIPTION = "The `libffi' library provides a portable, high level programming interface to various calling \
conventions.  This allows a programmer to call any function specified by a call interface description at run \
time. FFI stands for Foreign Function Interface.  A foreign function interface is the popular name for the \
interface that allows code written in one language to call code written in another language.  The `libffi' \
library really only provides the lowest, machine dependent layer of a fully featured foreign function interface.  \
A layer must exist above `libffi' that handles type conversions for values passed between the two languages."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3610bb17683a0089ed64055416b2ae1b"

inherit debian-package
require recipes-debian/sources/libffi.inc
FILESPATH_append = ":${COREBASE}/meta/recipes-support/libffi/libffi"

SRC_URI += "\
           file://not-win32.patch \
	   file://0001-mips-Use-compiler-internal-define-for-linux.patch \
           file://0001-mips-fix-MIPS-softfloat-build-issue.patch \
           file://0001-libffi-Support-musl-x32-build.patch \
           file://0001-testsuite-Prevent-deletion-of-test-binaries.patch \
           file://run-ptest \
           "

EXTRA_OECONF += "--disable-builddir"
EXTRA_OEMAKE_class-target = "LIBTOOLFLAGS='--tag=CC'"
inherit autotools texinfo ptest

FILES_${PN}-dev += "${libdir}/libffi-${PV}"

do_compile_ptest() {
    # Compile site.exp file
    oe_runmake -C testsuite site.exp
}

do_install_ptest() {
    cp -r --dereference ${B}/include/ ${D}${PTEST_PATH}/
    rm ${D}${PTEST_PATH}/include/Makefile*
    cp -r ${S}/testsuite/ ${D}${PTEST_PATH}/
    cp -r ${B}/testsuite/ ${D}${PTEST_PATH}/
    rm ${D}${PTEST_PATH}/testsuite/Makefile*
    cp -r ${B}/config.status ${D}${PTEST_PATH}/
    cp -r ${B}/fficonfig.h ${D}${PTEST_PATH}/
    cp -r ${B}/local.exp ${D}${PTEST_PATH}/

    sed -i \
        -e 's|^set srcdir .*|set srcdir "."|g' \
        -e 's|^set objdir .*|set objdir "."|g' \
        -e '/^set build_alias/s/^/#/' \
        -e '/^set build_triplet/s/^/#/' \
        -e '/^set CC_FOR_TARGET/s/^/#/' \
        -e '/^set CXX_FOR_TARGET/s/^/#/' \
        ${D}${PTEST_PATH}/testsuite/site.exp
}

RDEPENDS_${PN}-ptest += "bash dejagnu gcc-symlinks g++-symlinks binutils"

# Doesn't compile in MIPS16e mode due to use of hand-written
# assembly
MIPS_INSTRUCTION_SET = "mips"

BBCLASSEXTEND = "native nativesdk"

