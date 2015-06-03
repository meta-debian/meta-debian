#
# debian
#
SUMMARY = "Runtime configuration and test of XInput devices"

DESCRIPTION = "Xinput is an utility for configuring and testing XInput devices."

inherit debian-package autotools
DEBIAN_SECTION = "x11"
PR = "r0"
DPR = "0"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=881525f89f99cad39c9832bcb72e6fa5"

DEPENDS += "virtual/libx11 libxi libxext libxinerama libxrandr inputproto"
