SUMMARY = "ASN.1 library for Python"
DESCRIPTION = "This  is  an implementation  of  ASN.1  types  and codecs  in  Python \
programming language. It has been first written to support particular \
protocol (SNMP) but then generalized  to be suitable for a wide range \
of protocols based on ASN.1 specification."

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.rst;md5=280606d9c18f200e03e0c247ac61475a"

inherit debian-package
require recipes-debian/sources/pyasn1.inc
DEBIAN_UNPACK_DIR = "${WORKDIR}/pyasn1-${PV}"

do_install_append() {
	find ${D} -name *.pyc -delete
}
