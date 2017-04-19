#
# base recipe: meta/recipes-extended/sed/sed_4.2.2.bb
# base branch: jethro
#

SUMMARY = "The GNU sed stream editor"
DESCRIPTION = "sed reads the specified files or the standard input if no \
files are specified, makes editing changes according to a \
list of commands, and writes the results to the standard \
output."
HOMEPAGE = "http://www.gnu.org/software/sed/"

PR = "r2"

inherit debian-package
PV = "4.2.2"

LICENSE = "GPLv2+ & GPLv3+"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=f27defe1e96c2e1ecd4e0c9be8967949 \
	file://sed/sed.h;beginline=1;endline=17;md5=767ab3a06d7584f6fd0469abaec4412f \
	file://debian/copyright;md5=27c0cc3a8e6b182f66de46caf568799a \
"

SRC_URI += " \
    file://sed-add-ptest.patch \
    file://run-ptest \
"

RDEPENDS_${PN}-ptest += "make ${PN}"

inherit autotools texinfo update-alternatives gettext ptest

# Follow debian/rules
# --without-selinux: Don't use selinux support
EXTRA_OECONF = " \
	--without-included-regex \
	--without-selinux \
"

do_install () {
	autotools_do_install
	install -d ${D}${base_bindir}
	mv ${D}${bindir}/sed ${D}${base_bindir}/sed
	rmdir ${D}${bindir}/
}

ALTERNATIVE_${PN} = "sed"
ALTERNATIVE_LINK_NAME[sed] = "${base_bindir}/sed"
ALTERNATIVE_PRIORITY = "100"

TESTDIR = "testsuite"

do_compile_ptest() {
	oe_runmake -C ${TESTDIR} buildtest-TESTS
}

do_install_ptest() {
	oe_runmake -C ${TESTDIR} install-ptest BUILDDIR=${B} DESTDIR=${D}${PTEST_PATH} TESTDIR=${TESTDIR}
}

BBCLASSEXTEND = "native"
