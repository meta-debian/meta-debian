#
# base recipe: meta/recipes-devtools/perl/libxml-parser-perl_2.41.bb
# base branch: daisy
#

SUMMARY = "XML::Parser - A perl module for parsing XML documents"
LICENSE = "Artistic-1.0 | GPL-1.0"
LIC_FILES_CHKSUM = "file://README;beginline=2;endline=6;md5=c8767d7516229f07b26e42d1cf8b51f1"

DEPENDS += "expat expat-native"

PR = "r0"
inherit debian-package
PV = "2.41"

EXTRA_CPANFLAGS = "EXPATLIBPATH=${STAGING_LIBDIR} EXPATINCPATH=${STAGING_INCDIR} CC=${CC} LD=${LD} FULL_AR=${AR}"

inherit cpan

# fix up sub MakeMaker project as arguments don't get propagated though
# see https://rt.cpan.org/Public/Bug/Display.html?id=28632
do_configure_append() {
	sed 's:--sysroot=.*\(\s\|$\):--sysroot=${STAGING_DIR_TARGET} :g' -i Makefile Expat/Makefile
	sed 's:^FULL_AR = .*:FULL_AR = ${AR}:g' -i Expat/Makefile
}

do_compile() {
	export LIBC="$(find ${STAGING_DIR_TARGET}/${base_libdir}/ -name 'libc-*.so')"
	cpan_do_compile
}

do_compile_class-native() {
	cpan_do_compile
}

do_compile_append(){
	# Use encoding from debian
	for i in ${S}/debian/encodings/*.enc; do
		if [ -f ${S}/blib/lib/XML/Parser/Encodings/$(basename ${i}) ]; then
			rm ${S}/blib/lib/XML/Parser/Encodings/$(basename ${i})
		fi
		cp ${i} ${S}/blib/lib/XML/Parser/Encodings/
	done
}

FILES_${PN}-dbg += "${libdir}/perl/vendor_perl/*/auto/XML/Parser/Expat/.debug/"

BBCLASSEXTEND="native nativesdk"
