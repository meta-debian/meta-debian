#
# base recipe: meta/recipes-extended/groff/groff_1.22.3.bb
# base branch: warrior
#
SUMMARY = "GNU Troff software"
DESCRIPTION = "The groff (GNU troff) software is a typesetting package which reads plain text mixed with \
formatting commands and produces formatted output."
SECTION = "base"
HOMEPAGE = "http://www.gnu.org/software/groff/"
LICENSE = "GPLv3"

LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit debian-package
require recipes-debian/sources/groff.inc

SRC_URI += "file://0001-replace-perl-w-with-use-warnings.patch"

DEPENDS = "groff-native"
DEPENDS_class-native = ""
RDEPENDS_${PN} += "perl sed bash"

inherit autotools-brokensep texinfo multilib_script pkgconfig

MULTILIB_SCRIPTS = "${PN}:${bindir}/gpinyin ${PN}:${bindir}/groffer ${PN}:${bindir}/grog"

EXTRA_OECONF = "--without-x"
PARALLEL_MAKE = ""

CACHED_CONFIGUREVARS += "ac_cv_path_PERL='/usr/bin/env perl' ac_cv_path_BASH_PROG='/usr/bin/env bash'"

# Using some command from native for cross compile
EXTRA_OEMAKE_class-target += "GROFFBIN=${STAGING_BINDIR_NATIVE}/groff \
                              GROFF_BIN_PATH=${STAGING_BINDIR_NATIVE}"

do_install_append() {
	# Some distros have both /bin/perl and /usr/bin/perl, but we set perl location
	# for target as /usr/bin/perl, so fix it to /usr/bin/perl.
	for i in afmtodit mmroff gropdf pdfmom grog; do
		if [ -f ${D}${bindir}/$i ]; then
			sed -i -e '1s,#!.*perl,#! ${USRBINPATH}/env perl,' ${D}${bindir}/$i
		fi
	done
	if [ -e ${D}${libdir}/charset.alias ]; then
		rm -rf ${D}${libdir}/charset.alias
	fi

	# awk is located at /usr/bin/, not /bin/
	SPECIAL_AWK=`find ${D} -name special.awk`
	if [ -f ${SPECIAL_AWK} ]; then
		sed -i -e 's:#!.*awk:#! ${USRBINPATH}/awk:' ${SPECIAL_AWK}
	fi

	# not ship /usr/bin/glilypond and its releated files in embedded target system
	rm -rf ${D}${bindir}/glilypond
	rm -rf ${D}${libdir}/groff/glilypond
	rm -rf ${D}${mandir}/man1/glilypond*
}

do_install_append_class-native() {
	create_cmdline_wrapper ${D}/${bindir}/groff \
		-F${STAGING_DIR_NATIVE}${datadir_native}/groff/${PV}/font \
		-M${STAGING_DIR_NATIVE}${datadir_native}/groff/${PV}/tmac
}

FILES_${PN} += "${libdir}/${BPN}/site-tmac \
                ${libdir}/${BPN}/groffer/"

BBCLASSEXTEND = "native"
