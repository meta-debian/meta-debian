SUMMARY = "X11 Resource extension library"
DESCRIPTION = "\
libXRes provides an X Window System client interface to the Resource \
extension to the X protocol. \
. \
The Resource extension allows for X clients to see and monitor the X resource \
usage of various clients (pixmaps, et al). \
"
PR = "r0"
inherit debian-package pkgconfig
PV = "1.0.7"

LICENSE = "XFree86-1.0"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=8c89441a8df261bdc56587465e13c7fa"
inherit autotools
# Debian's source code isn't contains patch file
DEBIAN_PATCH_TYPE = "nopatch"

DEPENDS += "util-macros resourceproto"

do_install_append() {
	#remove .la files
	rm ${D}${libdir}/*.la
}
