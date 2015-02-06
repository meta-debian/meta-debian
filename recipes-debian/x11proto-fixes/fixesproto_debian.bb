require recipes-graphics/xorg-proto/fixesproto_5.0.bb

DPN = "x11proto-fixes"
inherit debian-package
DEBIAN_SECTION = "x11"
DPR = "0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=262a7a87da56e66dd639bf7334a110c6 \
file://xfixesproto.h;md5=fcfb0d4532a76861bee8ff4d61e8d397 \
"

# always try to apply patches in debian/patches by quilt
# see debian/xsfbs/xsfbs.mk
DEBIAN_PATCH_TYPE = "quilt"
