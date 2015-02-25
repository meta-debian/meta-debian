require recipes-devtools/m4/${PN}_1.4.17.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-devtools/m4/m4:"

inherit debian-package
DEBIAN_SECTION = "interpreters"

DPR = "0"

LICENSE = "GPL-3.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI += " \
file://ac_config_links.patch \
file://remove-gets.patch \
"

# no document, please
do_configure_prepend() {
	sed -i -e "/SUBDIRS/ s: doc : :" ${S}/Makefile.am
}

do_compile_prepend() {
	[ -d ${STAGING_INCDIR_NATIVE} ] || mkdir -p ${STAGING_INCDIR_NATIVE}
}
