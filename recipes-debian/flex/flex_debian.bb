#
# base-recipe: meta/recipes-devtools/flex/flex_2.5.38.bb
# base-branch: daisy
#

PR = "r0"

inherit debian-package autotools gettext ptest
PV = "2.5.39"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=e4742cf92e89040b39486a6219b68067"

SRC_URI += " \
file://do_not_create_pdf_doc.patch \
file://run-ptest \
"

M4 = "${bindir}/m4"
M4_class-native = "${STAGING_BINDIR_NATIVE}/m4"

EXTRA_OECONF += "ac_cv_path_M4=${M4}"
EXTRA_OEMAKE += "m4=${STAGING_BINDIR_NATIVE}/m4"

do_install_append_class-native() {
	create_wrapper ${D}/${bindir}/flex M4=${M4}
}

do_install_append_class-nativesdk() {
	create_wrapper ${D}/${bindir}/flex M4=${M4}
}

RDEPENDS_${PN} += "m4"
DEPENDS_${PN}-ptest += "bison-native flex-native"

do_compile_ptest() {
	for i in `find ${S}/tests/ -type d |grep -Ev "concatenated-options|reject|table-opts" | awk -F/ '{print $NF}'`; \
	  do oe_runmake -C ${S}/tests/$i -f ${B}/tests/$i/Makefile top_builddir=${B} FLEX=flex $i; \
	done
	oe_runmake -C ${S}/tests/test-reject -f ${B}/tests/test-reject/Makefile top_builddir=${B} FLEX=flex test-reject-nr test-reject-r test-reject-ser test-reject-ver
}

do_install_ptest() {
	for i in `find ${S}/tests/ -type d | awk -F/ '{print $NF}'`; \
	  do cp -r ${S}/tests/$i ${D}${PTEST_PATH}; \
	done
}
BBCLASSEXTEND = "native nativesdk"
