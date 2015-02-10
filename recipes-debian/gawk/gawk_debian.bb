require recipes-extended/gawk/gawk_4.0.2.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-extended/gawk/gawk-4.0.2:\
${COREBASE}/meta/recipes-extended/gawk/files:\
"

inherit debian-package
DEBIAN_SECTION = "interpreters"

DPR = "0"

LICENSE = "GPLv3"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

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

# Shipped installed files to corresponding folder
FILES_${PN} += "/usr/lib"

# Touch empty gawk.texi file according to debian/rules.
do_configure_prepend() {
	# solve libtool version mismatch
	find ${S} -name aclocal.m4 | xargs rm
	find ${S} -name ltversion.m4 | xargs rm

	# see debian/rules and comments in remove-doc.patch
	touch --date="Jan 01 2000" \
	${S}/doc/gawk.info ${S}/doc/gawk.texi ${S}/doc/gawkinet.info\ 
					      ${S}/doc/gawkinet.texi
}
