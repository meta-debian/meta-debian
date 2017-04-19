SUMMARY = "iCalendar library implementation in C"
DESCRIPTION = "\
	libical is an open source implementation of the IETF's iCalendar calendaring \
	and scheduling protocols (RFC 2445, 2446, and 2447). It parses iCal components \
	and provides a C API for manipulating the component properties, parameters, \
	and subcomponents. \
	"
HOMEPAGE = "http://freeassociation.sourceforge.net"
PR = "r0"
inherit debian-package
PV = "1.0"

LICENSE = "MPL-1.0 | LGPL-2.1"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=d4fc58309d8ed46587ac63bb449d82f8 \
	file://LICENSE;md5=d1a0891cd3e582b3e2ec8fe63badbbb6"

inherit cmake perlnative

PKG_${PN} = "${PN}1a"
RDEPENDS_${PN}-dev += "${PN}"
RDEPENDS_${PN} += "tzdata"
