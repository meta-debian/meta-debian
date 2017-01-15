SUMMARY = "X shared memory fences"
DESCRIPTION = "\
This library provides an interface to shared-memory fences for \
synchronization between the X server and direct-rendering clients. \
"
PR = "r0"
inherit debian-package pkgconfig
PV = "1.1"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=47e508ca280fde97906eacb77892c3ac"
inherit autotools
# Debian's source code isn't contains patch file
DEBIAN_PATCH_TYPE = "nopatch"

DEPENDS += "util-macros xproto"

do_install_append() {
	#remove .la files
	rm ${D}${libdir}/*.la
}
