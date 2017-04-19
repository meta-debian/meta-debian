SUMMARY = "GNU cpio -- a program to manage archives of files"
DESCRIPTION = "\
	GNU cpio is a tool for creating and extracting archives, or copying  \ 
	files from one place to another.  It handles a number of cpio formats\
	as well as reading and writing tar files. \
"
HOMEPAGE = "http://www.gnu.org/software/cpio/"
PR = "r0"
inherit debian-package
PV = "2.11+dfsg"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949"
inherit autotools-brokensep gettext
EXTRA_OECONF += "CPIO_MT_PROG=mt --bindir=${base_bindir}"

#remove info_TEXINFOS variable, because cpio.texi is not existed in ${S}/doc/
SRC_URI += "file://remove-info_TEXINFOS_debian.patch"

#install follow Debian jessie
do_install_append() {
	oe_runmake -C ${S}/po install-data-yes DESTDIR=${D}
	mv ${D}${base_bindir}/mt ${D}${base_bindir}/mt-gnu
	rm -r ${D}${libdir}
}
