PR = "r0"

inherit debian-package
PV = "2.4"

LICENSE = "GPL-3.0"

LIC_FILES_CHKSUM = "file://README;md5=f8fde40e05cd1e3026f89b8c754924c2"
DEPENDS = "quilt-native"

SRC_URI += "file://remove_strip.patch"

EXTRA_OEMAKE = "-e MAKEFLAGS="

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake prefix=${D}${prefix} mandir=${D}${mandir} install
}
