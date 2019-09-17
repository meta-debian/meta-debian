#
# base recipe: meta/recipes-extended/libmnl/libmnl_1.0.4.bb
# base branch: warrior
# base commit: 84f4010a836ac657a80921b5c39ae9e6a763f3e1
#

SUMMARY = "Minimalistic user-space Netlink utility library"
DESCRIPTION = "Minimalistic user-space library oriented to Netlink developers, providing \
    functions for common tasks in parsing, validating, and constructing both the Netlink header and TLVs."
HOMEPAGE = "http://www.netfilter.org/projects/libmnl/index.html"
SECTION = "libs"
LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

inherit debian-package
require recipes-debian/sources/libmnl.inc

DEBIAN_QUILT_PATCHES = ""

inherit autotools pkgconfig

BBCLASSEXTEND = "native"
