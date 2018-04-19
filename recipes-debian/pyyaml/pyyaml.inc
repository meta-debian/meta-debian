HOMEPAGE = "http://pyyaml.org/"
DPN = "pyyaml"
INC_PR = "r0"
inherit debian-package
PV = "3.11"

# Apply debian patch by quilt
DEBIAN_PATCH_TYPE = "quilt"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6015f088759b10e0bc2bf64898d4ae17"

KEEP_NONARCH_BASELIB = "1"

inherit distutils

DEPENDS += "libyaml"

# required by distutils/sysconfig.py to get config dir
export DEB_HOST_MULTIARCH
