#
# base recipe: meta/recipes-extended/bc/bc_1.07.1.bb
# base branch: master
# base commit: 028a292001f64ad86c6b960a05ba1f6fd72199de
#

SUMMARY = "Arbitrary precision calculator language"
HOMEPAGE = "http://www.gnu.org/software/bc/"

inherit debian-package
require recipes-debian/sources/bc.inc

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
	file://COPYING.LIB;md5=6a6a8e020838b23406c81b19c1d46df6 \
	file://bc/bcdefs.h;endline=26;md5=83710f912db7e902d45016ab235c586f \
	file://dc/dc.h;endline=20;md5=be0fc95c3503cb8116eea6acb63a5922 \
	file://lib/number.c;endline=30;md5=8529a3cebb13aca3fb46658b89cb90c7 \
"

DEPENDS = "bison-native flex-native ed-native"

FILESPATH_append = ":${COREBASE}/meta/recipes-extended/bc/bc"
SRC_URI += " \
    file://no-gen-libmath.patch \
    file://libmath.h \
"

inherit autotools-brokensep texinfo update-alternatives

PACKAGECONFIG ??= "readline"
PACKAGECONFIG[readline] = "--with-readline,--without-readline,readline"
PACKAGECONFIG[libedit] = "--with-libedit,--without-libedit,libedit"

do_compile_prepend() {
	cp -f ${WORKDIR}/libmath.h ${B}/bc/libmath.h
}

ALTERNATIVE_${PN} = "dc"
ALTERNATIVE_PRIORITY = "100"

BBCLASSEXTEND = "native nativesdk"
