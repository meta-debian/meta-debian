#
# base recipe: meta/recipes-graphics/freetype/freetype_2.5.2.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "2.5.2"

SUMMARY = "Freetype font rendering library"
DESCRIPTION = "FreeType is a software font engine that is designed to be \
small, efficient, highly customizable, and portable while capable of \
producing high-quality output (glyph images). It can be used in graphics \
libraries, display servers, font conversion tools, text image generation \
tools, and many other products as well."

LICENSE = "FreeType | GPLv2+"
LIC_FILES_CHKSUM = " \
file://docs/LICENSE.TXT;md5=c017ff17fc6f0794adf93db5559ccd56 \
file://docs/FTL.TXT;md5=d479e83797f699fe873b38dadd0fcd4c \
file://docs/GPLv2.TXT;md5=8ef380476f642c20ebf40fecb0add2ec \
"

inherit autotools-brokensep pkgconfig binconfig multilib_header

LIBTOOL = "${S}/builds/unix/${HOST_SYS}-libtool"
EXTRA_OEMAKE = "'LIBTOOL=${LIBTOOL}'"
EXTRA_OEMAKE_class-native = ""
EXTRA_OECONF = "--without-zlib --without-bzip2 CC_BUILD='${BUILD_CC}'"
TARGET_CPPFLAGS += "-D_FILE_OFFSET_BITS=64"


PACKAGECONFIG ??= ""
PACKAGECONFIG[pixmap] = "--with-png,--without-png,libpng"
# This results in a circular dependency so enabling is non-trivial
PACKAGECONFIG[harfbuzz] = "--with-harfbuzz,--without-harfbuzz,harfbuzz"

do_configure() {
	cd builds/unix
	libtoolize --force --copy
	aclocal -I .
	gnu-configize --force
	autoconf
	cd ${S}
	oe_runconf
}

do_configure_class-native() {
	(cd builds/unix && gnu-configize) || die "failure running gnu-configize"
	oe_runconf
}

do_compile_prepend() {
	${BUILD_CC} -o objs/apinames src/tools/apinames.c
}

do_install_append() {
	oe_multilib_header freetype2/config/ftconfig.h
}

BBCLASSEXTEND = "native"

# .orig.tar.gz is doubly-compressed
do_unpack_append() {
    bb.build.exec_func('do_uncompress', d)
}

do_uncompress() {
	cd ${S}
	PV_SRCPKG=$(head -n 1 ${S}/debian/changelog | \
					sed "s|.*(\([^()]*\)).*|\1|")
	PV_ORIG=$(echo $PV_SRCPKG | sed "s|-.*||")

	tar xjvf ${S}/freetype-$PV_ORIG.tar.bz2 -C ${S}
	mv ${S}/freetype-$PV_ORIG/* ${S}
	rm -r ${S}/freetype-$PV_ORIG
}

# Apply debian patches by quilt
DEBIAN_PATCH_TYPE = "quilt"
DEBIAN_QUILT_PATCHES = "${S}/debian/patches-freetype"

# remove trash files
do_debian_patch_prepend() {
	rm -rf ${DEBIAN_UNPACK_DIR}/.pc
}
