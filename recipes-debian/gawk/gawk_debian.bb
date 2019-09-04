#
# base recipe: meta/recipes-extended/gawk/gawk_4.2.1.bb
# base branch: master
# base commit: a5d1288804e517dee113cb9302149541f825d316
#

SUMMARY = "GNU awk text processing utility"
DESCRIPTION = "The GNU version of awk, a text processing utility. \
Awk interprets a special-purpose programming language to do \
quick and easy text pattern matching and reformatting jobs."
HOMEPAGE = "https://www.gnu.org/software/gawk/"

inherit debian-package
require recipes-debian/sources/gawk.inc

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS += "readline"

# run-ptest: test script for ptest
SRC_URI += " \
file://run-ptest \
"

# source format is 3.0 (quilt) but there is no debian/patches
DEBIAN_QUILT_PATCHES = ""

inherit autotools gettext update-alternatives

do_configure() {
	# Debian removed *.texi out of source code.
	# Touch them to pass compiling.
	oe_runconf
	touch --date="Jan 01 2000" \
		${S}/doc/gawktexi.in ${S}/doc/gawk.texi ${S}/doc/gawkinet.texi \
		${S}/doc/gawkworkflow.texi ${S}/doc/gawkworkflow.info \
		${S}/doc/gawk.info ${S}/doc/gawkinet.info ${S}/doc/sidebar.awk
}

do_install_append() {
	# Remove unwanted files.
	rm -f ${D}${bindir}/*awk-*
	rm -f ${D}${bindir}/awk
	# Remove fake info files
	rm -rf ${D}${datadir}/info
}

FILES_${PN} += "${datadir}/awk"

ALTERNATIVE_${PN} = "awk"
ALTERNATIVE_TARGET[awk] = "${bindir}/gawk"
ALTERNATIVE_PRIORITY = "100"

RDEPENDS_${PN}-ptest += "make"

inherit ptest

do_install_ptest() {
	mkdir ${D}${PTEST_PATH}/test
	for i in `grep -vE "@|^$|#|Gt-dummy" ${S}/test/Maketests |awk -F: '{print $1}'` Maketests inclib.awk; \
		do cp ${S}/test/$i* ${D}${PTEST_PATH}/test; \
	done
	sed -i -e 's|/usr/local/bin|${bindir}|g' \
	    -e 's|#!${base_bindir}/awk|#!${bindir}/awk|g' ${D}${PTEST_PATH}/test/*.awk
}

BBCLASSEXTEND = "native nativesdk"
