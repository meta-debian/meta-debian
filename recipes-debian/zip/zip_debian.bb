#
# base recipe: meta/recipes-extended/zip/zip_3.0.bb
# base branch: daisy
#

SUMMARY = "Compressor/archiver for creating and modifying .zip files"
HOMEPAGE = "http://www.info-zip.org"
SECTION = "console/utils"

PR = "r0"
inherit debian-package 
PV = "3.0"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=04d43c5d70b496c032308106e26ae17d"

EXTRA_OEMAKE = "'CC=${CC}' 'BIND=${CC}' 'AS=${CC} -c' 'CPP=${CPP}' \
		'CFLAGS=-I. -DUNIX ${CFLAGS}' 'INSTALL=install' \
		'BINFLAGS=0755' 'INSTALL_D=install -d'"

do_compile() {
	oe_runmake -f unix/Makefile flags IZ_BZIP2=no_such_directory
	sed -i 's#LFLAGS1=""#LFLAGS1="${LDFLAGS}"#' flags
	oe_runmake -f unix/Makefile generic IZ_BZIP2=no_such_directory
}

do_install() {
	oe_runmake -f unix/Makefile prefix=${D}${prefix} \
		   BINDIR=${D}${bindir} MANDIR=${D}${mandir}/man1 \
		   install
}

BBCLASSEXTEND = "native"

# zip.inc sets CFLAGS, but what Makefile actually uses is
# CFLAGS_NOOPT.  It will also force -O3 optimization, overriding
# whatever we set.
#
EXTRA_OEMAKE_append = " 'CFLAGS_NOOPT=-I. -DUNIX ${CFLAGS}'"

