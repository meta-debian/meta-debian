# base recipe: meta/recipes-support/libcap-ng/libcap-ng_0.7.9.bb
# base branch: warrior

SUMMARY = "An alternate posix capabilities library"
DESCRIPTION = "The libcap-ng library is intended to make programming \
with POSIX capabilities much easier than the traditional libcap library."
HOMEPAGE = "http://freecode.com/projects/libcap-ng"
SECTION = "base"
LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
		    file://COPYING.LIB;md5=e3eda01d9815f8d24aae2dbd89b68b06"

inherit debian-package
require recipes-debian/sources/libcap-ng.inc

FILESPATH_append = ":${COREBASE}/meta/recipes-support/libcap-ng/libcap-ng"
SRC_URI += "file://python.patch"

inherit lib_package autotools python3native

DEPENDS += "swig-native python3"

EXTRA_OECONF += "--with-python --with-python3"
EXTRA_OEMAKE += "PYLIBVER='python${PYTHON_BASEVERSION}${PYTHON_ABI}' PYINC='${STAGING_INCDIR}/${PYLIBVER}'"

PACKAGES += "${PN}-python"

FILES_${PN}-python = "${libdir}/python${PYTHON_BASEVERSION}"

BBCLASSEXTEND = "native"

do_install_append() {
	# Moving libcap-ng to base_libdir
	if [ ! ${D}${libdir} -ef ${D}${base_libdir} ]; then
		mkdir -p ${D}/${base_libdir}/
		mv -f ${D}${libdir}/libcap-ng.so.* ${D}${base_libdir}/
		relpath=${@os.path.relpath("${base_libdir}", "${libdir}")}
		ln -sf ${relpath}/libcap-ng.so.0.0.0 ${D}${libdir}/libcap-ng.so
	fi
}
