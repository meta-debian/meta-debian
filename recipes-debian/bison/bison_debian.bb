#
# base recipe: meta/recipes-devtools/bison/bison_2.7.1.bb
# base branch: daisy
#

PR = "r1"

inherit debian-package
PV = "3.0.2.dfsg"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
"

DEPENDS = "bison-native flex-native"
DEPENDS_class-native = "gettext-minimal-native"

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

# No point in hardcoding path to m4, just use PATH
EXTRA_OECONF += "M4=m4"

LDFLAGS_prepend_libc-uclibc = " -lrt "

inherit autotools gettext update-alternatives
acpaths = "-I ${S}/m4"

do_configure_prepend(){
	# Fix error gettext infrastructure mismatch
	cp ${STAGING_DATADIR_NATIVE}/gettext/po/Makefile.in.in ${S}/runtime-po/
}

# Follow debian, rename yacc to bison.yacc
do_install_append_class-target(){
	mv ${D}${bindir}/yacc ${D}${bindir}/bison.yacc
}

do_install_append_class-native() {
	create_wrapper ${D}/${bindir}/bison \
		BISON_PKGDATADIR=${STAGING_DATADIR_NATIVE}/bison
}

# Follow debian/bison.postinst
ALTERNATIVE_${PN} = "yacc"
ALTERNATIVE_PRIORITY = "100"
ALTERNATIVE_LINK_NAME[yacc] = "${bindir}/yacc"
ALTERNATIVE_TARGET[yacc] = "${bindir}/bison.yacc"

BBCLASSEXTEND = "native nativesdk"
