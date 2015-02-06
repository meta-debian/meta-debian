require recipes-graphics/xorg-proto/xextproto_7.3.0.bb

DPN = "x11proto-xext"
inherit debian-package
DEBIAN_SECTION = "x11"
DPR = "0"

S = "${WORKDIR}/git" 

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=86f273291759d0ba2a22585cd1c06c53"

# always try to apply patches in debian/patches by quilt
# see debian/xsfbs/xsfbs.mk
DEBIAN_PATCH_TYPE = "quilt"
