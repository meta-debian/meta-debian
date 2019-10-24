SUMMARY = "Python library exposing cryptographic recipes and primitives"

LICENSE = "Apache-2.0 | BSD"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=097f805837700cfac572ac274cd38124 \
    file://LICENSE.BSD;md5=5ae30ba4123bc4f2fa49aa0b0dce887b \
    file://LICENSE.APACHE;md5=4e168cce331e5c827d4c2b68a6200e1b \
"

inherit debian-package
require recipes-debian/sources/python-cryptography.inc
DEBIAN_UNPACK_DIR = "${WORKDIR}/cryptography-${PV}"

# There are no patch
DEBIAN_QUILT_PATCHES = ""

DEPENDS = "${PYTHON_PN}-cffi-native"

do_install_append() {
	find ${D} -name *.pyc -delete
}

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-asn1crypto \
    ${PYTHON_PN}-cffi \
    ${PYTHON_PN}-ipaddress \
"
