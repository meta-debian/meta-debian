require recipes-devtools/bison/${PN}_2.7.1.bb
FILESEXTRAPATHS_prepend = "${THISDIR}/files:${COREBASE}/meta/recipes-devtools/bison/bison:"

inherit debian-package
DEBIAN_SECTION = "devel"
DPR = "0"

LICENSE = "GPL-3.0"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
"

# Exclude following patches because they were tried to apply on 
# doc/Makefile.am but there is no such file:
# fix_cross_manpage_building.patch
# dont-depend-on-help2man.patch
# FIXME: file doc/bison.texi is missing, temporarily build without document
# and examples for minimal implementation with
# remove-document-examples-target.patch

BASE_SRC_URI = " \
file://m4.patch \
file://remove-document-examples-target.patch \
"

SRC_URI_class-native = " \
${DEBIAN_SRC_URI} \
${BASE_SRC_URI} \
"

SRC_URI += " \
${BASE_SRC_URI} \
"

# avoid a parallel build problem in src/yacc
PARALLEL_MAKE = ""

do_configure_prepend(){
	# Fix error gettext infrastructure mismatch
	cp ${STAGING_DATADIR_NATIVE}/gettext/po/Makefile.in.in ${S}/runtime-po/
}
