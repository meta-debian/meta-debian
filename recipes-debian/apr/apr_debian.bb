#
# base recipe: meta/recipes-support/apr/apr_1.4.8.bb
# base branch: daisy
#

SUMMARY = "Apache Portable Runtime Library"
DESCRIPTION = "APR is Apache's Portable Runtime Library, designed to be a support library \
that provides a predictable and consistent interface to underlying \
platform-specific implementations."
HOMEPAGE = "http://apr.apache.org/"

PR = "r1"

inherit debian-package
PV = "1.5.1"

DEPENDS = "util-linux"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4dfd4cd216828c8cae5de5a12f3844c8"

BBCLASSEXTEND = "native"

SRC_URI += " \
file://configure_fixes.patch \
file://configfix.patch \
file://run-ptest \
file://upgrade-and-fix-1.5.1.patch \
"

inherit autotools-brokensep lib_package binconfig multilib_header ptest

OE_BINCONFIG_EXTRA_MANGLE = " -e 's:location=source:location=installed:'"

# base on debian/rules
EXTRA_OECONF = " \
    --enable-layout=Debian \
    --includedir=${includedir}/apr-1.0 \
    --with-installbuilddir=${datadir}/apr-1.0/build \
    --enable-nonportable-atomics \
    --enable-allocator-uses-mmap \
"
CACHED_CONFIGUREVARS += "ac_cv_prog_AWK=mawk"

# apr_cv_mutex_robust_shared causes hangs in procmutex test on arm(el|hf) and alpha
CACHED_CONFIGUREVARS_append_arm = " apr_cv_mutex_robust_shared=no"
CACHED_CONFIGUREVARS_append_alpha = " apr_cv_mutex_robust_shared=no"

# some minimal cross-building support
CACHED_CONFIGUREVARS_append_class-target = " \
    ac_cv_file__dev_zero=yes \
    ac_cv_func_setpgrp_void=yes \
    apr_cv_epoll=yes \
    ac_cv_struct_rlimit=yes \
    apr_cv_tcp_nodelay_with_cork=yes \
    apr_cv_process_shared_works=yes \
"
# not support on cross compiling
CACHED_CONFIGUREVARS_append_class-target = " \
    apr_cv_mutex_robust_shared=no \
"

CACHED_CONFIGUREVARS_append_class-target = " \
    ${@base_conditional('SITEINFO_BITS','32','ac_cv_sizeof_struct_iovec=8','',d)}"
CACHED_CONFIGUREVARS_append_class-target = " \
    ${@base_conditional('SITEINFO_BITS','64','ac_cv_sizeof_struct_iovec=16','',d)}"

# Added to fix some issues with cmake. Refer to https://github.com/bmwcarit/meta-ros/issues/68#issuecomment-19896928
CACHED_CONFIGUREVARS += "apr_cv_mutex_recursive=yes"

do_configure() {
	cd ${S}
	./buildconf

	# We need to force the use of bash here. Otherwise, if apr is built with
	# /bin/sh -> /bin/bash, the resulting libtool will not work on systems
	# where /bin/sh -> /bin/dash.
	${CACHED_CONFIGUREVARS} CONFIG_SHELL=/bin/bash \
		/bin/bash ${CONFIGURE_SCRIPT} ${CONFIGUREOPTS} ${EXTRA_OECONF}
}

FILES_${PN}-dev += "${libdir}/apr.exp ${datadir}/apr-1.0/build/*"
RDEPENDS_${PN}-dev = "bash"

#for some reason, build/libtool.m4 handled by buildconf still be overwritten
#when autoconf, so handle it again.
do_configure_append() {
	sed -i -e 's/LIBTOOL=\(.*\)top_build/LIBTOOL=\1apr_build/' ${S}/build/libtool.m4
	sed -i -e 's/LIBTOOL=\(.*\)top_build/LIBTOOL=\1apr_build/' ${S}/build/apr_rules.mk
}

do_install_append() {
	oe_multilib_header apr-1.0/apr.h
	install -m 0755 ${S}/${HOST_SYS}-libtool ${D}${datadir}/apr-1.0/build/libtool

	ln -sf apr-1-config ${D}${bindir}/apr-config
}

# apr-config is a symlink, only apr-1-config should be modified
BINCONFIG_GLOB = "apr-1-config"
SSTATE_SCAN_FILES += "apr_rules.mk libtool"

SYSROOT_PREPROCESS_FUNCS += "apr_sysroot_preprocess"

apr_sysroot_preprocess () {
	d=${SYSROOT_DESTDIR}${datadir}/apr-1.0/build
	sed -i s,apr_builddir=.*,apr_builddir=,g $d/apr_rules.mk
	sed -i s,apr_builders=.*,apr_builders=,g $d/apr_rules.mk
	sed -i s,LIBTOOL=.*,LIBTOOL=${HOST_SYS}-libtool,g $d/apr_rules.mk
	sed -i s,\$\(apr_builders\),${STAGING_DATADIR}/apr-1.0/build,g $d/apr_rules.mk

	sed -i "s,cd /usr/share,cd ${STAGING_DATADIR},g" ${SYSROOT_DESTDIR}${bindir_crossscripts}/apr-1-config
}

DEBIANNAME_${PN} = "lib${PN}1"
DEBIANNAME_${PN}-dev = "lib${PN}1-dev"
DEBIANNAME_${PN}-dbg = "lib${PN}1-dbg"

do_compile_ptest() {
	cd ${S}/test
	oe_runmake
}

do_install_ptest() {
	t=${D}${PTEST_PATH}/test
	mkdir -p $t/.libs
	cp -r ${S}/test/data $t/
	cp -r ${S}/test/.libs/*.so $t/.libs/
	cp ${S}/test/proc_child $t/
	cp ${S}/test/readchild $t/
	cp ${S}/test/sockchild $t/
	cp ${S}/test/sockperf $t/
	cp ${S}/test/testall $t/
	cp ${S}/test/tryread $t/
}
