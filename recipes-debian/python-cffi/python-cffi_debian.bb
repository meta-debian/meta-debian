SUMMARY = "Foreign Function Interface for Python calling C code"
DESCRIPTION = "Convenient and reliable way of calling C code from Python.\n\
.\n\
The aim of this project is to provide a convenient and reliable way of calling\n\
C code from Python. It keeps Python logic in Python, and minimises the C\n\
required. It is able to work at either the C API or ABI level, unlike most\n\
other approaches, that only support the ABI level."
HOMEPAGE = "http://cffi.readthedocs.org/"
LICENSE = "MIT"
SECTION = "python"
DEPENDS = "python libffi python-pycparser-native"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5677e2fdbf7cdda61d6dd2b57df547bf"

PR = "r0"
inherit debian-package
PV = "0.8.6"

inherit setuptools

FILES_${PN} += "${PYTHON_SITEPACKAGES_DIR}"
RDEPENDS_${PN} += "python-pycparser"

BBCLASSEXTEND = "native"
