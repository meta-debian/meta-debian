require recipes-support/attr/${PN}_2.4.47.bb

FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-support/attr/files:"

inherit debian-package
DEBIAN_SECTION = "utils"
DPR = "0"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = " \
file://README;md5=62961c60371497ed780ec2bd0fc348fa \
file://libattr/libattr.c;md5=34b70c13046cbfa57a0194e9c97cc40b \
"

SRC_URI += " \
file://relative-libdir.patch;striplevel=0 \
file://run-ptest \
"
