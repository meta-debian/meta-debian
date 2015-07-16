#
# base recipe: meta-debian/recipes-debian/xorg-util/xorg-util-common.inc
# base branch: daisy
#

HOMEPAGE = "http://www.x.org"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=xorg"

INC_PR = "r0"

inherit debian-package autotools pkgconfig
DPN = "xutils-dev"

# Apply debian patch by quilt
DEBIAN_PATCH_TYPE = "quilt"

