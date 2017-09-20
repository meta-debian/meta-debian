#
# base recipe: meta/recipes-support/libcap/libcap_2.22.bb
# base branch: daisy
#

SUMMARY = "Library for getting/setting POSIX.1e capabilities"
HOMEPAGE = "http://sites.google.com/site/fullycapable/"

LICENSE = "BSD | GPLv2"
LIC_FILES_CHKSUM = "file://License;md5=3f84fd6f29d453a56514cb7e4ead25f1"

DPN = "libcap2"
DEPENDS = "attr hostperl-runtime-native"
DEPENDS += "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)}"
# attr and pam are disabled by EXTRA_OEMAKE_class-native
DEPENDS_class-native = "hostperl-runtime-native attr-native"

inherit debian-package
PV = "2.24"
inherit lib_package

PR = "r0"

# do NOT pass target cflags to host compilations
#
do_configure() {
	# libcap uses := for compilers, fortunately, it gives us a hint
	# on what should be replaced with ?=
	sed -e 's,:=,?=,g' -i Make.Rules
	sed -e 's,^BUILD_CFLAGS ?= $(.*CFLAGS),BUILD_CFLAGS := $(BUILD_CFLAGS),' -i Make.Rules

	# disable gperf detection
	sed -e '/shell gperf/cifeq (,yes)' -i libcap/Makefile
}

EXTRA_OEMAKE = " \
  LIBATTR=yes \
  PAM_CAP=${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'yes', 'no', d)} \
  INDENT= SYSTEM_HEADERS=${STAGING_INCDIR} RAISE_SETFCAP=no \
  lib=${@os.path.basename('${libdir}')} \
"
EXTRA_OEMAKE_class-native = " \
  LIBATTR=yes \
  PAM_CAP=no \
  INDENT= \
  RAISE_SETFCAP=no \
  lib=${@os.path.basename('${libdir}')} \
"

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake install DESTDIR="${D}" \
		prefix="${prefix}" \
		SBINDIR="${D}${base_sbindir}" \
		LIBDIR="${D}${base_libdir}"
}

# Install files follow Debian
do_install_append() {
	# libcap-dev:
	#   Move the development files from lib/ to usr/lib.
	mv ${D}${base_libdir}/*.a ${D}${libdir}
	rel_lib_prefix=`echo ${libdir} | sed 's,\(^/\|\)[^/][^/]*,..,g'`
	ln -sf ${rel_lib_prefix}${base_libdir}/libcap.so.2 ${D}${libdir}/libcap.so

	# Remove unwanted/unused files
	rm -rf ${D}${base_libdir}/*.so
}

PACKAGES += "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam-cap', '', d)}"

FILES_${PN}-bin += "${base_sbindir}"
# pam files
FILES_libpam-cap = " \
	${base_libdir}/security/*.so \
"
FILES_${PN}-dbg += "${base_libdir}/security/.debug/*.so"

DEBIANNAME_${PN} = "${PN}2"
DEBIANNAME_${PN}-bin = "${PN}2-bin"

BBCLASSEXTEND = "native nativesdk"
