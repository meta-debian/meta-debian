require recipes-graphics/xorg-lib/${PN}_1.3.3.bb

inherit debian-package
DEBIAN_SECTION = "x11"
DPR = "0"

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=49347921d4d5268021a999f250edc9ca"

# always try to apply patches in debian/patches by quilt
# see debian/xsfbs/xsfbs.mk
DEBIAN_PATCH_TYPE = "quilt"
