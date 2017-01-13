#
# base recipe: meta/recipes-extended/gawk/gawk_4.0.2.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "4.1.1+dfsg"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS += "readline"

# 
# remove-doc.patch:
# Remove "doc" dir from build targets
# since gawk.texi doesn't existed.
# See patch file for more detail.
#
# run-ptest:
# Patch file from reused recipes
SRC_URI += " \
file://remove-doc.patch \
file://run-ptest \
"

inherit autotools gettext update-alternatives

EXTRA_OECONF += "--disable-rpath --libexecdir=${libdir}"

# Touch empty gawk.texi file according to debian/rules.
do_configure_prepend() {
	# see debian/rules and comments in remove-doc.patch
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

FILES_${PN} += " \
	${datadir}/awk \
	${libdir}/awk \
"
FILES_${PN}-dbg += "${libdir}/awk/.debug"

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
