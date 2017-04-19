#
# base recipe: https://github.com/openembedded/openembedded-core/blob/master/
# meta/recipes-support/libcap-ng/libcap-ng_0.7.7.bb
# base branch: master
#

SUMMARY = "An alternate POSIX capabilities library"
DESCRIPTION = "This library implements the user-space interfaces to the POSIX\n\
1003.1e capabilities available in Linux kernels.  These capabilities are\n\
a partitioning of the all powerful root privilege into a set of distinct\n\
privileges.\n\
.\n\
The libcap-ng library is intended to make programming with POSIX\n\
capabilities much easier than the traditional libcap library."
HOMEPAGE = "http://people.redhat.com/sgrubb/libcap-ng"

PR = "r2"

inherit debian-package
PV = "0.7.4"

LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
		    file://COPYING.LIB;md5=e3eda01d9815f8d24aae2dbd89b68b06"

inherit lib_package autotools pythonnative

DEPENDS += "swig-native python"

# Prevent using python headers from host system
EXTRA_OEMAKE += "PYLIBVER='python${PYTHON_BASEVERSION}'"
do_configure_prepend() {
	sed -i -e "s: /usr/include/python: ${STAGING_INCDIR}/python:g" ${S}/configure.ac
	sed -i -e "s:-I/usr/include/\$(PYLIBVER):-I${STAGING_INCDIR}/python${PYTHON_BASEVERSION}:g" \
	          ${S}/bindings/python/Makefile.am
}

do_install_append() {
	# Follow debian/rules
	find ${D}${PYTHON_SITEPACKAGES_DIR} -name "*.la" -delete
}

PACKAGES =+ "python-cap-ng"

FILES_python-cap-ng = "${PYTHON_SITEPACKAGES_DIR}/*"
FILES_${PN}-dbg += "${PYTHON_SITEPACKAGES_DIR}/.debug"

# Keep compatible with meta layer
RPROVIDES_python-cap-ng = "${PN}-python"

# lib_package class provides libcap-ng-bin which is equal to libcap-ng-utils from Debian
RPROVIDES_${PN}-bin = "${PN}-utils"
DEBIANNAME_${PN}-bin = "${PN}-utils"

BBCLASSEXTEND = "native"
