SUMMARY = "library to load .glade files at runtime"
DESCRIPTION = "\
This library allows one to load externally stored user interfaces into \
programs. This allows alteration of the interface without recompilation \
of the program. \
The interfaces can also be edited with GLADE. \
"

PR = "r1"
inherit debian-package
PV = "2.6.4"

DPN = "libglade2"
LICENSE = "LGPLv2+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=55ca817ccb7d5b5b66355690e9abc605"

inherit autotools-brokensep pkgconfig
DEPENDS += "gtk+ atk libxml2 glib-2.0"

FILES_${PN} += "${datadir}/xml"
RDEPENDS_${PN}-dev += "gtk+-dev libxml2-dev python"
PKG_${PN} = "${DPN}-0"
PKG_${PN}-dev = "${DPN}-dev"

BBCLASSEXTEND = "native"
