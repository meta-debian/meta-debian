require recipes-extended/gzip/gzip.inc

inherit debian-package
require recipes-debian/sources/gzip.inc

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
	file://gzip.h;beginline=8;endline=20;md5=6e47caaa630e0c8bf9f1bc8d94a8ed0e \
"

FILESPATH_append = ":${COREBASE}/meta/recipes-extended/gzip/gzip-1.9:${COREBASE}/meta/recipes-extended/gzip/files"
SRC_URI += " \
    file://run-ptest \
"

PROVIDES_append_class-native = " gzip-replacement-native"

BBCLASSEXTEND = "native"

inherit ptest

do_install_ptest() {
	mkdir -p ${D}${PTEST_PATH}/src/build-aux
	cp ${S}/build-aux/test-driver ${D}${PTEST_PATH}/src/build-aux/
	mkdir -p ${D}${PTEST_PATH}/src/tests
	cp -r ${S}/tests/* ${D}${PTEST_PATH}/src/tests
	sed -e 's/^abs_srcdir = ..*/abs_srcdir = \.\./' \
	    -e 's/^top_srcdir = ..*/top_srcdir = \.\./' \
	    -e 's/^GREP = ..*/GREP = grep/'             \
	    -e 's/^AWK = ..*/AWK = awk/'                \
	    -e 's/^srcdir = ..*/srcdir = \./'           \
	    -e 's/^Makefile: ..*/Makefile: /'           \
	    -e 's,--sysroot=${STAGING_DIR_TARGET},,g'   \
	    -e 's|${DEBUG_PREFIX_MAP}||g' \
	    -e 's:${HOSTTOOLS_DIR}/::g'                 \
	    -e 's:${BASE_WORKDIR}/${MULTIMACH_TARGET_SYS}::g' \
	    ${B}/tests/Makefile > ${D}${PTEST_PATH}/src/tests/Makefile
	chmod 755 ${D}${PTEST_PATH}/src/tests/zgrep-abuse
	chmod 755 ${D}${PTEST_PATH}/src/tests/zgrep-binary
}
