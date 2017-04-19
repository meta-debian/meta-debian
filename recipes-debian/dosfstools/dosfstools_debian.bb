SUMMARY = "utilities for making and checking MS-DOS FAT filesystems"
DESCRIPTION = "The dosfstools package includes the mkfs.fat and fsck.fat utilities, which \
respectively make and check MS-DOS FAT filesystems."
HOMEPAGE = "https://github.com/dosfstools/dosfstools"

inherit debian-package
PV = "3.0.27"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

do_install() {
	oe_runmake "DESTDIR=${D}" "PREFIX=${prefix}" "SBINDIR=${base_sbindir}" install
}

BBCLASSEXTEND = "native"
