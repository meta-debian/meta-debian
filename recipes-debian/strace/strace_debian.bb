#
# base recipe: meta/recipes-devtools/strace/strace_4.9.bb
# base branch: master
# base commit: 5638e1fbead332c8143aa5aafbb7e6cb62ca59fc
#

SUMMARY = "System call tracing tool"
HOMEPAGE = "http://strace.sourceforge.net"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=124500c21e856f0912df29295ba104c7"

PR = "r0"
inherit debian-package
PV = "4.9"

# source format is 3.0 but there is no patch
DEBIAN_QUILT_PATCHES = ""

# strace-add-configure-options.patch:
#	Add options "aio" and "acl" to enable/disable libaio and acl support.
# git-version-gen:
#	Required by configure.ac.
# Makefile-ptest.patch & run-ptest:
#	Required to install and run ptest.
SRC_URI += " \
	file://strace-add-configure-options.patch \
	file://git-version-gen \
	file://Makefile-ptest.patch \
	file://run-ptest \
"

inherit autotools ptest
RDEPENDS_${PN}-ptest += "make coreutils grep gawk"

PACKAGECONFIG_class-target ?= "libaio ${@bb.utils.contains('DISTRO_FEATURES', 'acl', 'acl', '', d)}"

PACKAGECONFIG[libaio] = "--enable-aio,--disable-aio,libaio"
PACKAGECONFIG[acl] = "--enable-acl,--disable-acl,acl"
PACKAGECONFIG[libunwind] = "--with-libunwind, --without-libunwind, libunwind"

export INCLUDES = "-I. -I./linux"

TESTDIR = "tests"

do_configure_prepend() {
	cp ${WORKDIR}/git-version-gen ${S}
}

do_install_append() {
	# We don't ship strace-graph here because it needs perl
	rm ${D}${bindir}/strace-graph
}

do_compile_ptest() {
	oe_runmake -C ${TESTDIR} buildtest-TESTS
}

do_install_ptest() {
	oe_runmake -C ${TESTDIR} install-ptest BUILDDIR=${B} DESTDIR=${D}${PTEST_PATH} TESTDIR=${TESTDIR}
}

BBCLASSEXTEND = "native"
