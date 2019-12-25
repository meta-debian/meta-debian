SUMMARY = "Asynchronous I/O readiness notification library"
DESCRIPTION = "The ivykis library is a thin, portable wrapper around OS-provided \
mechanisms such as epoll(4), kqueue(2) and poll(2). It was mainly \
designed for building high-performance network applications, but can \
be used in any event-driver application that uses pollable file \
descriptors as its event sources."
HOMEPAGE = "http://libivykis.sourceforge.net/"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

inherit debian-package
require recipes-debian/sources/ivykis.inc

# There is no debian/patches
DEBIAN_QUILT_PATCHES = ""

SRC_URI += "file://avoid_segfault_on_arm64.patch"

inherit autotools
