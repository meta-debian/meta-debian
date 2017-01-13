#
# Base recipe: meta/recipes-graphics/xorg-lib/libxvmc_1.0.8.bb
# Base branch: daisy
#

SUMMARY = "XvMC: X Video Motion Compensation extension library"

DESCRIPTION = "XvMC extends the X Video extension (Xv) and enables \
hardware rendered motion compensation support."

require xorg-lib-common.inc
PV = "1.0.8"

PR = "${INC_PR}.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=0a207f08d4961489c55046c9a5e500da \
file://wrapper/XvMCWrapper.c;endline=26;md5=5151daa8172a3f1bb0cb0e0ff157d9de"

DEPENDS += "libxext libxv videoproto"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"

PKG_${PN} = "${PN}1"
PKG_${PN}-dbg = "${PN}1-dbg"
