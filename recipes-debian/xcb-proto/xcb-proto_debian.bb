require recipes-graphics/xorg-proto/${PN}_1.10.bb

inherit debian-package
DEBIAN_SECTION = "libdevel"
DPR = "0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=d763b081cb10c223435b01e00dc0aba7"

# no patch related-rule
DEBIAN_PATCH_TYPE = "nopatch"
