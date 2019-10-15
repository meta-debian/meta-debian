SUMMARY = "provides useful locks - Python 2.7"
DESCRIPTION = "Fasteners is a Python package that provides useful locks. It includes locking \
decorator (that acquires instance objects lock(s), acquires on method entry \
and releases on method exit), reader-writer locks, inter-process locks and \
generic lock helpers."

inherit debian-package
require recipes-debian/sources/python-fasteners.inc

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=4476c4be31402271e101d9a4a3430d52"

inherit setuptools

DEBIAN_QUILT_PATCHES = ""

RDEPENDS_${PN} += "python python-six python-monotonic"
