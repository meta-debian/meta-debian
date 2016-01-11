# base recipe: poky/meta/recipes-devtools/perl/liburi-perl_1.60.bb 
# base branch: jethro

SUMMARY = "Perl module to manipulate and access URI strings"
DESCRIPTION = "This package contains the URI.pm module with friends. \
The module implements the URI class. URI objects can be used to access \
and manipulate the various components that make up these strings."

PR = "r0"
inherit debian-package

SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"

LIC_FILES_CHKSUM = "file://README;beginline=26;endline=30;md5=6c33ae5c87fd1c4897714e122dd9c23d"

DEPENDS += "perl"

EXTRA_CPANFLAGS = "EXPATLIBPATH=${STAGING_LIBDIR} EXPATINCPATH=${STAGING_INCDIR}"

inherit cpan

do_compile() {
	export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
	cpan_do_compile
}

# install file follow content of package name of jessie
do_install_append() {
	install -d ${D}${datadir}/perl5/
	mv ${D}${libdir}/perl/vendor_perl/5.20.2/URI ${D}${datadir}/perl5/
	mv ${D}${libdir}/perl/vendor_perl/5.20.2/URI.pm ${D}${datadir}/perl5/
	rm -r ${D}${libdir}
}

# ship file for package liburi-perl
FILES_${PN} =+ " ${datadir}"

BBCLASSEXTEND = "native"
