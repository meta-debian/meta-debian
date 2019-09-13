inherit debian-package
require recipes-debian/sources/xorgproto.inc
DEBIAN_PATCH_TYPE="nopatch"

require xorg-proto-common.inc

SUMMARY = "XCalibrate: Touchscreen calibration headers"

DESCRIPTION = "This package provides the headers and specification documents defining \
the core protocol and (many) extensions for the X Window System"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING-x11proto;md5=b9e051107d5628966739a0b2e9b32676"

BBCLASSEXTEND = "native nativesdk"
