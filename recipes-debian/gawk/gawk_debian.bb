#
# base recipe: meta/recipes-extended/gawk/gawk_4.0.2.bb
# base branch: daisy
#

SUMMARY = "GNU awk text processing utility"
DESCRIPTION = "The GNU version of awk, a text processing utility. \
Awk interprets a special-purpose programming language to do \
quick and easy text pattern matching and reformatting jobs."
HOMEPAGE = "https://www.gnu.org/software/gawk/"

inherit debian-package
PV = "4.1.4+dfsg"
DPR = "-1"
DSC_URI = "${DEBIAN_MIRROR}/main/g/${BPN}/${BPN}_${PV}${DPR}.dsc;md5sum=f65a7d4ce42f8a2876efe6930039eb6b"
DEBIAN_UNPACK_DIR = "${WORKDIR}/${BPN}-4.1.4"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS += "readline"

# run-ptest: test script for ptest
SRC_URI += " \
file://run-ptest \
"

inherit autotools gettext update-alternatives

do_configure() {
	# Debian removed *.texi out of source code.
	# Touch them to pass compiling.
	oe_runconf
	touch --date="Jan 01 2000" \
		${S}/doc/gawktexi.in ${S}/doc/gawk.texi ${S}/doc/gawkinet.texi \
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

inherit ptest

do_install_ptest() {
	mkdir ${D}${PTEST_PATH}/test
	for i in `grep -vE "@|^$|#|Gt-dummy" ${S}/test/Maketests |awk -F: '{print $1}'` Maketests; \
		do cp ${S}/test/$i* ${D}${PTEST_PATH}/test; \
	done
}

BBCLASSEXTEND = "native nativesdk"
