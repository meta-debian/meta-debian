SUMMARY = "C library providing queue, tree, I/O and utility functions"
DESCIPTION = "a C library (with some C++ bindings available) that provides data \
structures and functions commonly needed, such as maps, deques, linked lists, \
string formatting and autoresizing, option and config file parsing, type \
checking casts and more. This package contains the shared libraries."
HOMEPAGE =  "http://libhx.sourceforge.net/"

PR = "r0"

inherit debian-package

PV = "3.21"

LICENSE = "(LGPLv3 | LGPLv2.1+) & GPLv3+"
LIC_FILES_CHKSUM = " \
	file://LICENSE.GPL3;md5=f27defe1e96c2e1ecd4e0c9be8967949 \
	file://LICENSE.LGPL2;md5=fbc093901857fcd118f065f900982c24 \
	file://LICENSE.LGPL3;md5=6a6a8e020838b23406c81b19c1d46df6"

inherit autotools

do_install_append(){
	install -d ${D}${datadir}/lintian/overrides
	install -m 0644 ${S}/debian/libhx-dev.lintian-overrides ${D}${datadir}/lintian/overrides/libhx-dev
}

FILES_${PN}-dev += "${datadir}/lintian/overrides/libhx-dev"
FILES_${PN}-dbg += "${base_libdir}/security/.debug/*"
