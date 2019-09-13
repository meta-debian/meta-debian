SUMMARY = "XML::Parser - A perl module for parsing XML documents"
HOMEPAGE = "https://libexpat.github.io/"
SECTION = "libs"
LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=2;endline=6;md5=c8767d7516229f07b26e42d1cf8b51f1"

inherit debian-package
require recipes-debian/sources/libxml-parser-perl.inc
DEBIAN_UNPACK_DIR = "${WORKDIR}/XML-Parser-2.44"

DEPENDS += "expat"

EXTRA_CPANFLAGS = "EXPATLIBPATH=${STAGING_LIBDIR} EXPATINCPATH=${STAGING_INCDIR} CC='${CC}' LD='${CCLD}' FULL_AR='${AR}'"

inherit cpan ptest-perl

# fix up sub MakeMaker project as arguments don't get propagated though
# see https://rt.cpan.org/Public/Bug/Display.html?id=28632
do_configure_append_class-target() {
	sed -E \
	    -e 's:-L${STAGING_LIBDIR}::g' -e 's:-I${STAGING_INCDIR}::g' \
	    -i Makefile Expat/Makefile
}

do_configure_append() {
	sed -e 's:--sysroot=.*\(\s\|$\):--sysroot=${STAGING_DIR_TARGET} :g' \
	    -i Makefile Expat/Makefile
	sed 's:^FULL_AR = .*:FULL_AR = ${AR}:g' -i Expat/Makefile
	# make sure these two do not build in parallel
	sed 's!^$(INST_DYNAMIC):!$(INST_DYNAMIC): $(BOOTSTRAP)!' -i Expat/Makefile
}

do_compile() {
	export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
	cpan_do_compile
}

do_compile_class-native() {
	cpan_do_compile
}

do_install_ptest() {
	sed -i -e "s:/usr/local/bin/perl:/usr/bin/perl:g" ${B}/samples/xmlstats
	sed -i -e "s:/usr/local/bin/perl:/usr/bin/perl:g" ${B}/samples/xmlfilter
	sed -i -e "s:/usr/local/bin/perl:/usr/bin/perl:g" ${B}/samples/xmlcomments
	sed -i -e "s:/usr/local/bin/perl:/usr/bin/perl:g" ${B}/samples/canonical
	cp -r ${B}/samples ${D}${PTEST_PATH}
	chown -R root:root ${D}${PTEST_PATH}/samples
}

BBCLASSEXTEND="native nativesdk"
