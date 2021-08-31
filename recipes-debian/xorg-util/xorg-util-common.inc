#
# base recipe: meta-debian/recipes-debian/xorg-util/xorg-util-common.inc
# base branch: daisy
#

HOMEPAGE = "http://www.x.org"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=xorg"

INC_PR = "r0"

LICENSE = "MIT-X"

inherit debian-package autotools pkgconfig
PV = "7.7+3"
DPN = "xutils-dev"

XORG_PN = "${BPN}"

S = "${DEBIAN_UNPACK_DIR}/${XORG_PN}"

# Apply debian patch by quilt
DEBIAN_PATCH_TYPE = "quilt"
