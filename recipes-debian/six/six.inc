SUMMARY = "Python 2 and 3 compatibility library"
DESCRIPTION = " \
	Six is a Python 2 and 3 compatibility library. It provides utility \
	functions for smoothing over the differences between the Python versions \
	with the goal of writing Python code that is compatible on both Python \
	versions."
HOMEPAGE = "http://pythonhosted.org/six/"

INC_PR = "r0"

inherit debian-package
PV = "1.8.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=530f34047c21ad45b3199946dd90bea5"

# Empty DEBIAN_QUILT_PATCHES to avoid error "debian/patches not found"
DEBIAN_QUILT_PATCHES = ""

KEEP_NONARCH_BASELIB = "1"

# required by distutils/sysconfig.py to get config dir
export DEB_HOST_MULTIARCH
