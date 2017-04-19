# base recipe: http://cgit.openembedded.org/meta-openembedded/tree/meta-oe/\
#recipes-support/xfsprogs/xfsprogs_3.1.7.bb?h=danny
# base branch: danny

DESCRIPTION = "XFS Filesystem Utilities"
HOMEPAGE = "http://oss.sgi.com/projects/xfs"

LICENSE = "GPLv2"
LICENSE_libhandle = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=dbdb5f4329b7e7145de650e9ecd4ac2a"

DEPENDS = "util-linux"

PR = "r1"
inherit debian-package
PV = "3.2.1"

# Debian's source code isn't contains patch file
DEBIAN_PATCH_TYPE = "nopatch"

# remove-install-as-user.patch:
#     fix bug invalid user
# xfsprogs-generate-crctable-which-is-moved-into-runti.patch:
#     Fix error when cross compile:
#       | /bin/bash: ./gen_crc32table: cannot execute binary file: Exec format error
SRC_URI += " \
    file://remove-install-as-user.patch \
    file://xfsprogs-generate-crctable-which-is-moved-into-runti.patch \
"

inherit autotools

B = "${S}"

EXTRA_OECONF = "--enable-gettext=no"
do_configure () {
	export DEBUG="-DNDEBUG"
	oe_runconf
}

LIBTOOL = "${HOST_SYS}-libtool"
EXTRA_OEMAKE = "'LIBTOOL=${LIBTOOL}'"
TARGET_CC_ARCH += "${LDFLAGS}"
PARALLEL_MAKE = ""

do_install () {
	export DIST_ROOT=${D}
	oe_runmake install
	# needed for xfsdump
	oe_runmake install-dev

	# Do not use absolute path for linking library
	libname=`readlink ${D}${libdir}/libhandle.so | xargs basename`
	rel_lib_prefix=`echo ${libdir} | sed 's,\(^/\|\)[^/][^/]*,..,g'`
	ln -sf ${rel_lib_prefix}${base_libdir}/${libname} ${D}${libdir}/libhandle.so
}

PACKAGES =+ "xfslibs-dev"
FILES_xfslibs-dev = " \
        ${base_libdir}/libhandle.la \
        ${base_libdir}/libhandle.so \
        ${libdir}/libhandle.la \
        ${libdir}/libhandle.so \
        ${includedir}/xfs/* \
    "
