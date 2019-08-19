#
# base-recipe: meta/recipes-devtools/flex/flex_2.6.0.bb
# base-branch: master
# base-commit: d886fa118c930d0e551f2a0ed02b35d08617f746
#
SUMMARY = "Flex (The Fast Lexical Analyzer)"
DESCRIPTION = "Flex is a fast lexical analyser generator.  Flex is a tool for generating programs that recognize \
lexical patterns in text."
HOMEPAGE = "http://sourceforge.net/projects/flex/"

inherit debian-package
require recipes-debian/sources/flex.inc

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=e4742cf92e89040b39486a6219b68067"

DEPENDS = "${@bb.utils.contains('PTEST_ENABLED', '1', 'bison-native flex-native', '', d)}"

SRC_URI += "file://run-ptest \
           file://0001-tests-add-a-target-for-building-tests-without-runnin.patch \
           ${@bb.utils.contains('PTEST_ENABLED', '1', '', 'file://disable-tests.patch', d)} \
           "
FILESEXTRAPATHS =. "${COREBASE}/meta/recipes-devtools/flex/flex:"

inherit autotools gettext texinfo ptest

# There is no debian patches
DEBIAN_PATCH_TYPE = "nopatch"

M4 = "${bindir}/m4"
M4_class-native = "${STAGING_BINDIR_NATIVE}/m4"
EXTRA_OECONF += "ac_cv_path_M4=${M4}"
EXTRA_OECONF += "ac_cv_func_reallocarray=no"
EXTRA_OEMAKE += "m4=${STAGING_BINDIR_NATIVE}/m4"

EXTRA_OEMAKE += "${@bb.utils.contains('PTEST_ENABLED', '1', 'FLEX=${STAGING_BINDIR_NATIVE}/flex', '', d)}"

do_configure_prepend() {
	# Make sure scan.c is newer than scan.l. Avoid error:
	# | WARNING: 'flex' is missing on your system
	# |          You should only need it if you modified a '.l' file.
	# | ...
	# | make[2]: *** [Makefile:1462: scan.c] Error 127
	touch ${S}/src/scan.c
}

do_install_append_class-native() {
	create_wrapper ${D}/${bindir}/flex M4=${M4}
}

do_install_append_class-nativesdk() {
	create_wrapper ${D}/${bindir}/flex M4=${M4}
}

PACKAGES =+ "${PN}-libfl"

FILES_${PN}-libfl = "${libdir}/libfl.so.* ${libdir}/libfl_pic.so.*"

RDEPENDS_${PN} += "m4"
RDEPENDS_${PN}-ptest += "bash gawk"

do_compile_ptest() {
	oe_runmake -C ${B}/tests -f ${B}/tests/Makefile top_builddir=${B} INCLUDES=-I${S}/src buildtests
}

do_install_ptest() {
	mkdir -p ${D}${PTEST_PATH}/build-aux/
	cp ${S}/build-aux/test-driver ${D}${PTEST_PATH}/build-aux/
	cp -r ${S}/tests/* ${D}${PTEST_PATH}
	cp -r ${B}/tests/* ${D}${PTEST_PATH}
	sed -e 's,--sysroot=${STAGING_DIR_TARGET},,g' \
	    -e 's|${DEBUG_PREFIX_MAP}||g' \
	    -e 's:${HOSTTOOLS_DIR}/::g' \
	    -e 's:${RECIPE_SYSROOT_NATIVE}::g' \
	    -e 's:${BASE_WORKDIR}/${MULTIMACH_TARGET_SYS}::g' \-e 's/^Makefile:/_Makefile:/' \
	    -e 's/^srcdir = \(.*\)/srcdir = ./' -e 's/^top_srcdir = \(.*\)/top_srcdir = ./' \
	    -e 's/^builddir = \(.*\)/builddir = ./' -e 's/^top_builddir = \(.*\)/top_builddir = ./' \
	    -e "s:^abs_builddir = \(.*\):abs_builddir = ${PTEST_PATH}:" -e "s:^abs_top_builddir = \(.*\):abs_top_builddir = ${PTEST_PATH}:" \
	    -e "s:^abs_srcdir = \(.*\):abs_srcdir = ${PTEST_PATH}:" -e "s:^abs_top_srcdir = \(.*\):abs_top_srcdir = ${PTEST_PATH}:" \
	    -i ${D}${PTEST_PATH}/Makefile
}

BBCLASSEXTEND = "native nativesdk"
