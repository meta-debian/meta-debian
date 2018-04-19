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

KEEP_NONARCH_BASELIB = "1"

# required by distutils/sysconfig.py to get config dir
export DEB_HOST_MULTIARCH
