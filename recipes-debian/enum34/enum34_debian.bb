SUMMARY = "backport of Python 3.4's enum package"
DESCRIPTION = "PEP 435 adds an enumeration to Python 3.4. This module provides a \
backport of that data type for older Python versions. It defines two \
enumeration classes that can be used to define unit sets of names and \
values: Enum and IntEnum."

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://enum/LICENSE;md5=0a97a53a514564c20efd7b2e8976c87e"

inherit debian-package
require recipes-debian/sources/enum34.inc

# There is no patch
DEBIAN_QUILT_PATCHES = ""

inherit setuptools

do_install_append() {
	find ${D} -name *.pyc -delete
}

RPROVIDES_${PN} += "python-enum34"
PKG_${PN} = "python-enum34"
