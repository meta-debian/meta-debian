SUMMARY = "Python library exposing cryptographic recipes and primitives"
DESCRIPTION = "The cryptography library is designed to be a \"one-stop-shop\" for\n\
all your cryptographic needs in Python.\n\
.\n\
As an alternative to the libraries that came before it, cryptography\n\
tries to address some of the issues with those libraries:\n\
 - Lack of PyPy and Python 3 support.\n\
 - Lack of maintenance.\n\
 - Use of poor implementations of algorithms (i.e. ones with known\n\
   side-channel attacks).\n\
 - Lack of high level, "Cryptography for humans", APIs.\n\
 - Absence of algorithms such as AES-GCM.\n\
 - Poor introspectability, and thus poor testability.\n\
 - Extremely error prone APIs, and bad defaults.\n\
.\n\
This package contains the Python 2 version of cryptography."
HOMEPAGE = "https://cryptography.io/"
LICENSE = "Apache-2.0"
SECTION = "python"
DEPENDS = "python python-cffi-native python-ply-native six-native"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

PR = "r0"
inherit debian-package
PV = "0.6.1"

inherit setuptools

RDEPENDS_${PN} += "libssl1.0.0 python-cffi python-six"

BBCLASSEXTEND = "native"
