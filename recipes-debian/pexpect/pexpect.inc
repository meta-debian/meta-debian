INC_PR = "r0"

inherit debian-package
PV = "3.2"
DPN = "pexpect"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c25d9a0770ba69a9965acc894e9f3644"

DEBIAN_QUILT_PATCHES = ""

KEEP_NONARCH_BASELIB = "1"

# required by distutils/sysconfig.py to get config dir
export DEB_HOST_MULTIARCH
