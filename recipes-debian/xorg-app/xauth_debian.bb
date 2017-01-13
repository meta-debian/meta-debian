#
# base recipe: meta/recipes-graphics/xorg-app/xauth_1.0.9.bb
# base branch: master
# base commit: e5423dbf6fd1deefa560c46743f4b7525e22f0b2
#

require xorg-app-common.inc
PV = "1.0.9"
SUMMARY = "X authority utilities"
DESCRIPTION = "X application to edit and display the authorization \
information used in connecting to the X server."
PR = "${INC_PR}.0"

LIC_FILES_CHKSUM = "file://COPYING;md5=5ec74dd7ea4d10c4715a7c44f159a40b"

DEPENDS += "libxau libxext libxmu"
PE = "1"

DEBIAN_PATCH_TYPE = "quilt"
