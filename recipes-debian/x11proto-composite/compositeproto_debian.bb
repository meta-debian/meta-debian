require recipes-graphics/xorg-proto/compositeproto_0.4.2.bb

DPN = "x11proto-composite"
inherit debian-package
DEBIAN_SECTION = "x11"
DPR = "0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=2c4bfe136f4a4418ea2f2a96b7c8f3c5"

# There is no debian/patches
DEBIAN_PATCH_TYPE = "nopatch"
