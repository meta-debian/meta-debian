SUMMARY = "RPM installation environment"
DESCRIPTION = "\
	This is a tool for bootstrapping a basic RPM-based distribution of \
	GNU/Linux. It is comparable in purpose to the standard Debian \
	debootstrap utility, but works upon RPM-based distributions instead. \
	Rinse can setup 32 and 64-bit installations of: \
		* CentOS \
		* Scientific Linux CERN \
		* Fedora \
		* OpenSUSE"
HOMEPAGE = "http://collab-maint.alioth.debian.org/rinse/"

LICENSE = "GPL-1.0 | Artistic-2.0"
LIC_FILES_CHKSUM = "file://bin/rinse;beginline=165;endline=174;md5=dd2cdd3c695a6d3cc7450140acc46571"

PR = "r0"

inherit debian-package
PV = "3.0.9"

do_compile(){
	oe_runmake
}

do_install(){
	oe_runmake 'DESTDIR=${D}' install
}

