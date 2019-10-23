SUMMARY = "Python bindings to libsodium"
DESCRIPTION = "PyNaCl is a Python binding to the Networking and Cryptography library (in the \
form of libsodium), a crypto library with the stated goal of improving \
usability, security and speed."

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8cc789b082b3d97e1ccc5261f8594d3f"

inherit debian-package
require recipes-debian/sources/python-nacl.inc
DEBIAN_UNPACK_DIR = "${WORKDIR}/PyNaCl-${PV}"

DEPENDS = "${PYTHON_PN}-cffi-native"

do_install_append() {
	find ${D} -name *.pyc -delete
}
