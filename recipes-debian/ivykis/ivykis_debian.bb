SUMMARY = "Asynchronous I/O readiness notification library"
DESCRIPTION = "\
	The ivykis library is a thin, portable wrapper around OS-provided \
	mechanisms such as epoll(4), kqueue(2) and poll(2). It was mainly \
	designed for building high-performance network applications, but can \
	be used in any event-driver application that uses pollable file \
	descriptors as its event sources"
HOMEPAGE = "http://libivykis.sourceforge.net/"

PR = "r0"
inherit debian-package
PV = "0.36.2"

LICENSE = "LGPL-2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

inherit autotools

#install follow Debian jessie
do_install_append() {
	rm ${D}${libdir}/libivykis.la
	LINKLIB=$(basename $(readlink ${D}${libdir}/libivykis.so))
	chmod 0644 ${D}${libdir}/${LINKLIB}
}
#correct the sub-package name
DEBIANNAME_${PN}-dbg = "libivykis0-dbg"
