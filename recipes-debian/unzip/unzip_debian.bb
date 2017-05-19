#
# base reicpe: meta/recipes-extended/unzip/unzip_6.0.bb
# base branch: daisy
#

SUMMARY = "Utilities for extracting and viewing files in .zip archives"
HOMEPAGE = "http://www.info-zip.org"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=94caec5a51ef55ef711ee4e8b1c69e29"

PR = "r0"
inherit debian-package
PV = "6.0"

DEPENDS = "bzip2"

SRC_URI += "\
	file://avoid-strip.patch \
	file://define-ldflags.patch \
"

# Makefile uses CF_NOOPT instead of CFLAGS.  We lifted the values from
# Makefile and add CFLAGS.  Optimization will be overriden by unzip
# configure to be -O3.
#
EXTRA_OEMAKE = "-e MAKEFLAGS= STRIP=true LF2='' \
                'CF_NOOPT=-I. -Ibzip2 -DUNIX ${CFLAGS}'"

export LD = "${CC}"
LD_class-native = "${CC}"

# Follow debian/rules
DEFINES = "-DACORN_FTYPE_NFS -DWILD_STOP_AT_DIR -DLARGE_FILE_SUPPORT \
	-DUNICODE_SUPPORT -DUNICODE_WCHAR -DUTF8_MAYBE_NATIVE -DNO_LCHMOD \
	-DDATE_FORMAT=DF_YMD -DUSE_BZIP2 -DIZ_HAVE_UXUIDGID -DNOMEMCPY \
	-DNO_WORKING_ISPRINT"

EXTRA_OEMAKE += "D_USE_BZ2=-DUSE_BZIP2 L_BZ2=-lbz2 \
		LF2='${LDFLAGS}' \
		'CF=${CFLAGS} ${CPPFLAGS} -I. ${DEFINES}'"

do_compile() {
	oe_runmake -f unix/Makefile unzips
}

do_install() {
	oe_runmake -f unix/Makefile install prefix=${D}${prefix}
	install -d ${D}${mandir}
	mv ${D}${prefix}/man ${D}${datadir}/
}

BBCLASSEXTEND = "native"
