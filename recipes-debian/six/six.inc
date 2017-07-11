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

# need to export these variables for python runtime
# fix error:
#       PREFIX = os.path.normpath(sys.prefix).replace( os.getenv("BUILD_SYS"), os.getenv("HOST_SYS") )
#       TypeError: Can't convert 'NoneType' object to str implicitly
export BUILD_SYS
export HOST_SYS
