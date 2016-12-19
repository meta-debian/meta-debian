HOMEPAGE = "http://pyyaml.org/"
DPN = "pyyaml"
INC_PR = "r0"
inherit debian-package

# Apply debian patch by quilt
DEBIAN_PATCH_TYPE = "quilt"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6015f088759b10e0bc2bf64898d4ae17"

inherit distutils

DEPENDS += "libyaml"
