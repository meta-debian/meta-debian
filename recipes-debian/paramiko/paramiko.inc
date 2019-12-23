SUMMARY = "Make ssh v2 connections"
DESCRIPTION = "This is a library for making SSH2 connections (client or server). \
Emphasis is on using SSH2 as an alternative to SSL for making secure \
connections between Python scripts.  All major ciphers and hash methods \
are supported.  SFTP client and server mode are both supported too."

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://LICENSE;md5=fd0120fc2e9f841c73ac707a30389af5"

inherit debian-package
require recipes-debian/sources/paramiko.inc
DEBIAN_UNPACK_DIR = "${WORKDIR}/paramiko-${PV}"

do_install_append() {
	find ${D} -name *.pyc -delete
}

RDEPENDS_${PN} += " \
    ${PYTHON_PN}-pyasn1 \
    ${PYTHON_PN}-bcrypt \
    ${PYTHON_PN}-cryptography \
    ${PYTHON_PN}-nacl \
"
