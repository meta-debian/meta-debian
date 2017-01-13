#
# No base recipe
#

DESCRIPTION = "datrie is an implementation of double-array structure \
for representing trie, as proposed by Junichi Aoe."
HOMEPAGE = "http://linux.thai.net/projects/libthai"

PR = "r0"

inherit debian-package autotools pkgconfig
PV = "0.2.8"

LICENSE = "LGPL-2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

# source package has no patch.
DEBIAN_QUILT_PATCHES = ""

# Add more packages
PACKAGES =+ "${PN}1-bin"

FILES_${PN}1-bin += "${bindir}/*"

# Rename package follow Debian
DEBIANNAME_${PN} = "${PN}1"

BBCLASSEXTEND += "native"
