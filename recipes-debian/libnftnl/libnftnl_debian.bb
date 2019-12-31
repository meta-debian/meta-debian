# base recipe: meta-openembedded/meta-networking/recipes-filter/libnftnl/libnftnl_1.1.1.bb
# base branch: warrior

SUMMARY = "Library for low-level interaction with nftables Netlink's API over libmnl"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=79808397c3355f163c012616125c9e26"
SECTION = "libs"
DEPENDS = "libmnl"

inherit debian-package
require recipes-debian/sources/libnftnl.inc

DEBIAN_QUILT_PATCHES = ""

SRC_URI += "file://0001-Move-exports-before-symbol-definition.patch \
	    file://0002-avoid-naming-local-function-as-one-of-printf-family.patch \
	   "

inherit autotools pkgconfig
