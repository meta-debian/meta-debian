SUMMARY = "library for curve design"
DESCRIPTION = "This library provides a mechanism for drawing smooth contours with \
constant curvature at the spline joins."
HOMEPAGE = "http://sourceforge.net/projects/libspiro/"

PR = "r0"

inherit debian-package
PV = "20071029"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://gpl.txt;md5=751419260aa954499f7abaabaa882bbe"

inherit autotools-brokensep

DEBIANNAME_${PN}-dbg = "${PN}0-dbg"

BBCLASSEXTEND = "native"
