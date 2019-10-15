#
# base recipe: http://git.yoctoproject.org/cgit/cgit.cgi/meta-maker/tree/recipes-python/monotonic/python-monotonic_1.3.bb?h=master
#
SUMMARY = "implementation of time.monotonic() - Python 2.x"
DESCRIPTION= "This module provides a monotonic() function which returns the value (in \
fractional seconds) of a clock which never goes backwards. On Python 3.3 or \
newer, monotonic will be an alias of time.monotonic from the standard library. \
On older versions, it will fall back to an equivalent implementation: \
GetTickCount64 on Windows, mach_absolute_time on OS X, and clock_gettime(3) \
on Linux/BSD."

inherit debian-package
require recipes-debian/sources/python-monotonic.inc

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d2794c0df5b907fdace235a619d80314"

DEBIAN_QUILT_PATCHES = ""

inherit setuptools

RDEPENDS_${PN} += "${PYTHON_PN}-ctypes ${PYTHON_PN}-io ${PYTHON_PN}-re ${PYTHON_PN}-threading"
