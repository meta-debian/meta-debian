require recipes-graphics/xorg-proto/damageproto_1.2.1.bb

DPN = "x11proto-damage"
inherit debian-package
DEBIAN_SECTION = "x11"
DPR = "0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d5f5a2de65c3a84cbde769f07a769608"

# setting for no-patch case
DEBIAN_PATCH_TYPE = "nopatch"
