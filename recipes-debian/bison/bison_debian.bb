#
# base recipe: meta/recipes-devtools/bison/bison_3.0.4.bb
# base branch: master
# base commit: d886fa118c930d0e551f2a0ed02b35d08617f746
#

SUMMARY = "YACC-compatible parser generator"
DESCRIPTION = "Bison is a general-purpose parser generator that converts a\n\
grammar description for an LALR(1) context-free grammar into a C\n\
program to parse that grammar.  Once you are proficient with Bison, you\n\
may use it to develop a wide range of language parsers, from those used\n\
in simple desk calculators to complex programming languages.\n\
.\n\
Bison is upward compatible with Yacc: all properly-written Yacc\n\
grammars ought to work with Bison with no change.  Anyone familiar with\n\
Yacc should be able to use Bison with little trouble.  Documentation of\n\
the program is in the bison-doc package."
HOMEPAGE = "http://www.gnu.org/software/bison/"

inherit debian-package
require recipes-debian/sources/bison.inc
DEBIAN_UNPACK_DIR = "${WORKDIR}/${BPN}-${REPACK_PV}"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
"


# FIXME: file doc/bison.texi is missing, temporarily build without document
# and examples for minimal implementation with
# remove-document-examples-target.patch
SRC_URI += "\
	file://remove-document-examples-target.patch \
"

DEPENDS = "bison-native flex-native"

# No point in hardcoding path to m4, just use PATH
EXTRA_OECONF += "M4=m4"

LDFLAGS_prepend_libc-uclibc = " -lrt "

inherit autotools gettext texinfo

# The automatic m4 path detection gets confused, so force the right value
acpaths = "-I ${S}/m4"

do_install_append_class-native() {
	create_wrapper ${D}/${bindir}/bison \
		BISON_PKGDATADIR=${STAGING_DATADIR_NATIVE}/bison
}

BBCLASSEXTEND = "native nativesdk"
