SUMMARY = "Internet Protocol bandwidth measuring tool"
DESCRIPTION = "\
	Iperf is a modern alternative for measuring TCP and UDP bandwidth performance, \
	allowing the tuning of various parameters and characteristics. \
"
HOMEPAGE = "http://iperf.sourceforge.net/"
PR = "r0"
inherit debian-package
PV = "2.0.5+dfsg1"

LICENSE = "NCSA"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=e8478eae9f479e39bc34975193360298"

#fix the dupplicate install dist-man-Mans (iperf.1)
SRC_URI += "file://dist-man-install_debian.patch"
inherit autotools
