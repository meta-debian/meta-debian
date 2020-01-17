SUMMARY = "utility functions from BSD systems"
DESCRIPTION = "This library provides some C functions such as strlcpy() that are commonly \
available on BSD systems but not on others like GNU systems."
HOMEPAGE = "https://libbsd.freedesktop.org/"
LICENSE = "BSD-4-Clause & ISC & PD"
LIC_FILES_CHKSUM = "file://COPYING;md5=b552602fda69e34c753d26de383f33c5"

inherit debian-package
require recipes-debian/sources/libbsd.inc

# There is no debian/patches
DEBIAN_QUILT_PATCHES = ""

inherit autotools pkgconfig

BBCLASSEXTEND = "native nativesdk"
