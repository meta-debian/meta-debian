#
# base recipe: meta/recipes-support/attr/attr_2.4.47.bb
# base branch: daisy
#

require ea-acl.inc
PV = "2.4.47"
PR = "r0"

LICENSE = "LGPLv2.1+ & GPLv2"
LICENSE_${PN} = "GPLv2"
LICENSE_lib${DPN} = "LGPLv2.1+"
LIC_FILES_CHKSUM = " \
	file://doc/COPYING;md5=2d0aa14b3fce4694e4f615e30186335f \
	file://doc/COPYING.LGPL;md5=b8d31f339300bc239d73461d68e77b9c \
	file://libattr/libattr.c;md5=34b70c13046cbfa57a0194e9c97cc40b \
	file://attr/attr.c;beginline=1;endline=17;md5=be0403261f0847e5f43ed5b08d19593c \
"

DEPENDS = "ncurses virtual/libintl"

SRC_URI += " \
	file://relative-libdir.patch;striplevel=0 \
	file://run-ptest \
"

# libdir should point to .la
do_install_append() {
	sed -i ${D}${libdir}/libattr.la -e \
		s,^libdir=\'${base_libdir}\'$,libdir=\'${libdir}\',
}

inherit ptest

do_install_ptest() {
	tar -cf - test/ --exclude ext | ( cd ${D}${PTEST_PATH} && tar -xf - )
	mkdir ${D}${PTEST_PATH}/include
	for i in builddefs buildmacros buildrules; \
		do cp ${S}/include/$i ${D}${PTEST_PATH}/include/; \
	done
	sed -e 's|; @echo|; echo|' -i ${D}${PTEST_PATH}/test/Makefile
}

RDEPENDS_${PN}-ptest = "coreutils perl-module-filehandle perl-module-getopt-std perl-module-posix"

BBCLASSEXTEND = "native nativesdk"
