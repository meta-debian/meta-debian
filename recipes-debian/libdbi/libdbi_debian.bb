# base recipe: https://github.com/sysmocom/meta-telephony/tree/master/recipes-misc/libdbi/libdbi.inc 
# base branch: master

# libdbi OE build file
# Copyright (C) 2005, Koninklijke Philips Electronics NV.  All Rights Reserved
# Released under the MIT license (see packages/COPYING)

PR = "r0"

inherit debian-package
PV = "0.9.0"

DESCRIPTION = "Database Independent Abstraction Layer for C"
HOMEPAGE = "http://libdbi.sourceforge.net/"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8045f3b8f929c1cb29a1e3fd737b499"

SECTION = "libs"

inherit autotools

EXTRA_OECONF = "--disable-docs"

do_configure () {
	autotools_do_configure
}
