# base recipe: http://cgit.openembedded.org/cgit.cgi/meta-openembedded/tree/\
#meta-filesystems/recipes-utils/dmapi/dmapi_2.2.12.bb?h=master
# base branch: master

SUMMARY = "Library functions to get attribute bits"
DESCRIPTION = "The Data Management API (DMAPI/XDSM) allows implementation \
               of hierarchical storage management software with no kernel \
               modifications as well as high-performance dump programs \
               without requiring "raw" access to the disk and knowledge \
               of filesystem structures.This interface is implemented by \
               the libdm library."

HOMEPAGE = "http://oss.sgi.com/projects/xfs"

PR = "r0"
inherit debian-package
PV = "2.2.10"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=1678edfe8de9be9564d23761ae2fa794"

DEPENDS = "xfsprogs"

# Debian's source code isn't contains patch file
DEBIAN_PATCH_TYPE = "nopatch"

# fix bug invalid user
SRC_URI += "file://remove-install-as-user.patch"

inherit autotools-brokensep

PARALLEL_MAKE = ""
EXTRA_OEMAKE += "LIBTOOL="${HOST_SYS}-libtool --tag=CC" V=1"

do_install () {
	export DIST_ROOT=${D}
	install -d ${D}${libdir}
	oe_runmake install install-dev PKG_DEVLIB_DIR=${libdir}
	
	install -d ${D}${base_libdir}
	ln -s ..${libdir}/libdm.a ${D}${base_libdir}/libdm.a
	ln -s ..${libdir}/libdm.la ${D}${base_libdir}/libdm.la
	rm ${D}${libdir}/libdm.so 
	ln -s ..${libdir}/libdm.so.0 ${D}${base_libdir}/libdm.so
	ln -s ../..${base_libdir}/libdm.so ${D}${libdir}/libdm.so
}

