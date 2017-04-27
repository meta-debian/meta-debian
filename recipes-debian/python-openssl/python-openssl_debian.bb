SUMMARY = "Python 2 wrapper around the OpenSSL library"
DESCRIPTION = "High-level wrapper around a subset of the OpenSSL library, includes \n\
.\n\
  * SSL.Connection objects, wrapping the methods of Python's portable\n\
    sockets\n\
  * Callbacks written in Python\n\
  * Extensive error-handling mechanism, mirroring OpenSSL's error\n\
    codes\n\
.\n\
A lot of the object methods do nothing more than calling a\n\
corresponding function in the OpenSSL library."
HOMEPAGE = "https://github.com/pyca/pyopenssl"
LICENSE = "Apache-2.0"
SECTION = "python"
DEPENDS = "python openssl"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

PR = "r0"
inherit debian-package
PV = "0.14"
DPN = "pyopenssl"

inherit allarch setuptools

RDEPENDS_${PN} += "python-cryptography python-six"

BBCLASSEXTEND = "native"
