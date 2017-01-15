#
# base recipe: http://cgit.openembedded.org/cgit.cgi/meta-openembedded/tree/meta-perl/recipes-perl/libhtml/libhtml-parser-perl_3.71.bb
# base commit: 0d3e50468c0908ebbd5e986fa0963d46c06ab370
#

SUMMARY = "collection of modules that parse HTML text documents"
DESCRIPTION = "HTML::Parser is a collection of modules useful for handling HTML documents.\n\
These modules used to be part of the libwww-perl distribution, but are now\n\
unbundled in order to facilitate a separate development track.\n\
.\n\
Objects of the HTML::Parser class will recognize markup and separate it from\n\
content data. As different kinds of markup are recognized, the corresponding\n\
event handler is invoked. The document to be parsed may also be supplied in\n\
arbitrary chunks, making on-the-fly parsing of network documents possible."
HOMEPAGE = "https://metacpan.org/release/HTML-Parser/"

PR = "r0"

inherit debian-package
PV = "3.71"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=59;md5=e530c3545d6f8db92fb5f9a29b420d00"

EXTRA_CPANFLAGS = "EXPATLIBPATH=${STAGING_LIBDIR} EXPATINCPATH=${STAGING_INCDIR}"

inherit cpan

do_compile() {
	export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
	cpan_do_compile
}

BBCLASSEXTEND = "native"
