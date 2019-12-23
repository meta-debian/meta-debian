SUMMARY = "password hashing library for Python"
DESCRIPTION = "bcrypt is a Python module which provides a password hashing method based on \
the Blowfish password hashing algorithm, as described in \
"A Future-Adaptable Password Scheme" by Niels Provos and David Mazieres: \
http://static.usenix.org/events/usenix99/provos.html."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8f7bb094c7232b058c7e9f2e431f389c"

inherit debian-package
require recipes-debian/sources/python-bcrypt.inc
DEBIAN_UNPACK_DIR = "${WORKDIR}/bcrypt-${PV}"

# There is no patch
DEBIAN_QUILT_PATCHES = ""

DEPENDS = "${PYTHON_PN}-cffi-native"

do_install_append() {
	find ${D} -name *.pyc -delete
}

RDEPENDS_${PN} += "${PYTHON_PN}-cffi"
