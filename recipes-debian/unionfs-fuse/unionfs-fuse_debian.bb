SUMMARY = "Fuse implementation of unionfs"
DESCRIPTION = "\
	This is another unionfs implementation using filesystem in userspace (fuse)"

PR = "r0"
inherit debian-package
PV = "0.24"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7e5a37fce17307066eec6b23546da3b3"

#Makefile_debian.patch:
#	This patch is correct the path to install output files
SRC_URI += "file://Makefile_debian.patch"

inherit autotools-brokensep
DEPENDS += "fuse"
RDEPENDS_${PN} = "fuse"

