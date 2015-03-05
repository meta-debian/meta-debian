require recipes-devtools/m4/${PN}_1.4.17.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-devtools/m4/m4:"

inherit debian-package
DEBIAN_SECTION = "interpreters"

DPR = "1"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI += "\
	file://ac_config_links.patch\
	file://remove-gets.patch\
"

# Change timestamp of some files to avoid remaking them (which require
# automake to be installed)
# 'make -d' command gives clue on this 
do_configure_prepend() {
	# To avoid running aclocal
	touch ${S}/aclocal.m4

	# To avoid running automake
	touch ${S}/Makefile.in

	# To avoid rebuilding manpages.
	touch ${S}/doc/m4.1
}

do_compile_prepend() {
	[ -d ${STAGING_INCDIR_NATIVE} ] || mkdir -p ${STAGING_INCDIR_NATIVE}
}
