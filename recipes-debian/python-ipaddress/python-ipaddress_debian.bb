SUMMARY = "Backport of Python 3 ipaddress module"

LICENSE = "PSFv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7f538584cc3407bf76042def7168548a"

inherit debian-package
require recipes-debian/sources/python-ipaddress.inc
DEBIAN_UNPACK_DIR = "${WORKDIR}/ipaddress-${PV}"

inherit setuptools

do_install_append() {
	find ${D} -name *.pyc -delete
}
