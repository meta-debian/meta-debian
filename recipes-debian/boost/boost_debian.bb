include boost-debian.inc
include boost.inc

#SRC_URI += "file://arm-intrinsics.patch"
#
# debian
#
inherit debian-package
DEBIAN_SECTION = "libs"
DPN = "boost1.55"
