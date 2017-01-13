DESCRIPTION = "\
	Runtime library for configuration file parser,required for running \
	programs with dsh config file support. \
	dsh uses this library to parse configuration files."

PR = "r0"
inherit debian-package
PV = "0.20.13"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit autotools-brokensep
DEBIAN_PATCH_TYPE = "nopatch"

#configure follow debian/rules
EXTRA_OECONF = "--with-versioned-symbol"

DEBIANNAME_${PN}-dev = "${PN}1-dev"
