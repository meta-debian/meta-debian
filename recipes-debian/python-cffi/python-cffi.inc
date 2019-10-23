SUMMARY = "Foreign Function Interface for Python calling C code"
DESCRIPTION = "Convenient and reliable way of calling C code from Python."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5677e2fdbf7cdda61d6dd2b57df547bf"

inherit debian-package
require recipes-debian/sources/python-cffi.inc
DEBIAN_UNPACK_DIR = "${WORKDIR}/cffi-${PV}"

# There is no patch
DEBIAN_QUILT_PATCHES = ""

DEPENDS = "libffi"

do_install_append() {
	find ${D} -name *.pyc -delete
}

PACKAGES =+ "${PN}-backend"
FILES_${PN}-backend = "${PYTHON_SITEPACKAGES_DIR}/_cffi_backend*.so"

RDEPENDS_${PN} += "${PYTHON_PN}-pycparser"
RDEPENDS_${PN}_append_class-target = " ${PN}-backend"

BBCLASSEXTEND = "native"
