include boost-1.55.0.inc
include boost.inc

#SRC_URI += "file://arm-intrinsics.patch"
#
# debian
#
inherit debian-package
DEBIAN_SECTION = "libs"
DPR = "0"
DPN = "boost1.55"
