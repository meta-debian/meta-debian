#
# No base recipe
#

DESCRIPTION = "LibThai is a set of Thai language support routines aimed to ease \
developers' tasks to incorporate Thai language support in their \
applications. It includes important Thai-specific functions e.g. word \
breaking, input and output methods as well as basic character and \
string supports. LibThai is an Open Source and collaborative effort \
initiated by Thai Linux Working Group and opened for all contributors."
HOMEPAGE = "http://linux.thai.net/projects/libthai/"

PR = "r0"

inherit debian-package autotools pkgconfig
PV = "0.1.21"

LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

# source package has no patch.
DEBIAN_QUILT_PATCHES = ""

EXTRA_OECONF += "--enable-dict --enable-shared"

DEPENDS += "libdatrie-native libdatrie"

# Create more packages follow Debian
PACKAGES =+ "${PN}-data"

FILES_${PN}-data += "${datadir}/${BPN}/thbrk.tri"
