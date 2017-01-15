#
# Base recipe: meta/recipes-devtools/pax-utils/pax-utils_0.7.bb
# Base branch: daisy
# Base commit: 9e4aad97c3b4395edeb9dc44bfad1092cdf30a47
#

SUMMARY = "Security-focused ELF files checking tool"
DESCRIPTION = "This is a small set of various PaX aware and related \
utilities for ELF binaries. It can check ELF binary files and running \
processes for issues that might be relevant when using ELF binaries \
along with PaX, such as non-PIC code or executable stack and heap."
HOMEPAGE = "http://www.gentoo.org/proj/en/hardened/pax-utils.xml"

PR = "r0"
# pax-utils in Debian require libcap to build
# on Linux
DEPENDS = "libcap"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"

inherit debian-package
PV = "0.8.1"

# Add option to compile with libcap
do_compile_prepend() {
	USE_CAP=yes
	export USE_CAP
}

do_install() {
	oe_runmake PREFIX=${D}${prefix} DESTDIR=${D}  install
}

BBCLASSEXTEND = "native"
