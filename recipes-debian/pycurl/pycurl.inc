INC_PR = "r0"

inherit debian-package
PV = "7.19.5"
DPN = "pycurl"

DEPENDS = "curl"

LICENSE = "LGPLv2.1+ | MIT"
LIC_FILES_CHKSUM = "\
	file://README.rst;beginline=166;endline=181;md5=57e5ab0c0f964533fc59d93dec5695bb \
	file://COPYING-LGPL;md5=3579a9fd0221d49a237aaa33492f988c \
	file://COPYING-MIT;md5=e8200955c773b2a0fd6cea36ea5e87be"

# need to export these variables for python runtime
# fix error:
#       PREFIX = os.path.normpath(sys.prefix).replace( os.getenv("BUILD_SYS"), os.getenv("HOST_SYS") )
#       TypeError: Can't convert 'NoneType' object to str implicitly
export BUILD_SYS
export HOST_SYS
