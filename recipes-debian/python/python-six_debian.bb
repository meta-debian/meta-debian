SUMMARY = "Python 2 and 3 compatibility library (Python 2 interface)"
DESCRIPTION = "Six is a Python 2 and 3 compatibility library. It provides utility \
functions for smoothing over the differences between the Python versions \
with the goal of writing Python code that is compatible on both Python \
versions."

inherit debian-package
require recipes-debian/sources/six.inc

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=83e0f622bd5ac7d575dbd83d094d69b5"

inherit setuptools

DEBIAN_QUILT_PATCHES = ""

DEBIAN_UNPACK_DIR = "${WORKDIR}/six-${PV}"

RDEPENDS_${PN} += "python-core python-io python-lang python-shell"
