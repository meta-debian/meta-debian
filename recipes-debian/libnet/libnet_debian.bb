# base recipe: meta-openembedded/meta-oe/recipes-connectivity/libnet/libnet_1.2-rc3.bb
# base branch: warrior

SUMMARY = "A packet dissection and creation library"
HOMEPAGE = "https://github.com/libnet/libnet"
SECTION = "libs"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://doc/COPYING;md5=fb43d5727b2d3d1238545f75ce456ec3"

inherit debian-package
require recipes-debian/sources/libnet.inc

DEPENDS = "libpcap"

SRC_URI += "file://0001-Support-musl-libc-remove-support-for-glibc-2.1.patch"

inherit autotools binconfig
