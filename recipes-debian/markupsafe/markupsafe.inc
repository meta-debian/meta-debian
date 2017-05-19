SUMMARY = "HTML/XHTML/XML string library for Python"
DESCRIPTION = "MarkupSafe is a Python library implementing a unicode subclass that is \
 aware of HTML escaping rules. It can be used to implement automatic \
 string escaping."
HOMEPAGE = "http://pypi.python.org/pypi/MarkupSafe"

DPN = "markupsafe"
INC_PR = "r0"
inherit debian-package
PV = "0.23"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c6d1adcf45d69359f256c1cea3254127"
inherit setuptools

# Empty DEBIAN_QUILT_PATCHES to avoid error "debian/patches not found"
DEBIAN_QUILT_PATCHES = ""

# need to export these variables for python runtime
# fix error:
#       PREFIX = os.path.normpath(sys.prefix).replace( os.getenv("BUILD_SYS"), os.getenv("HOST_SYS") )
#       TypeError: Can't convert 'NoneType' object to str implicitly
export BUILD_SYS
export HOST_SYS

do_install_append() {
	# remove unwanted files
	rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/markupsafe/*.pyc \
	       ${D}${PYTHON_SITEPACKAGES_DIR}/markupsafe/__pycache__ \
	       ${D}${PYTHON_SITEPACKAGES_DIR}/markupsafe/_speedups.c
}
