# base recipe: http://git.yoctoproject.org/cgit/cgit.cgi/meta-selinux/tree/recipes-extended/ustr/ustr_1.0.4.bb?h=danny
# base branch: danny
 
SUMMARY = "Micro String API - for C"
HOMEPAGE = "http://www.and.org/ustr/"
DESCRIPTION = "Micro string library, very low overhead from plain strdup() (Ave. 44% for \
0-20B strings). Very easy to use in existing C code. At it's simplest you can \
just include a single header file into your .c and start using it."

PR = "r2"
inherit debian-package
PV = "1.0.4"

LICENSE = "MIT | LGPLv2+ | BSD"
LIC_FILES_CHKSUM = " \
	file://LICENSE;md5=c79c6e2ae13418d16d7dc82df960a1e7 \
	file://LICENSE_BSD;md5=ceb504b0b6471e76cc9cb32cfb150f3c \
	file://LICENSE_LGPL;md5=d8045f3b8f929c1cb29a1e3fd737b499 \
	file://LICENSE_MIT;md5=c61e779b782608472bd87593c3c3916f \
    "

# for apply patches
DEBIAN_PATCH_TYPE = "quilt"

# ustr-makefile-fix.patch:
# 	Don't run autoconf_64b or autoconf_vsnprintf
# 	to avoid error when cross-compile.
SRC_URI_append_class-target = " \
    file://ustr-makefile-fix.patch \
"

inherit lib_package siteinfo

# The debian class renames output packages so that they follow the Debian naming policy
LEAD_SONAME = "libustr-1.0.so.1"

# ${datadir}/libustr-dev/ contains .h files and .c files
FILES_${PN}-dev += " \
	${bindir}/ustr-import \
	${libexecdir}/ustr-*/ustr-import-* \
	${datadir}/libustr-dev/ \
    "

do_compile() {
	oe_runmake all-shared \
		mlib=${SITEINFO_BITS}
}

do_install() {
	oe_runmake install-multilib-linux  \
		mlib=${SITEINFO_BITS} \
		DESTDIR=${D} LDCONFIG=/bin/true
}

# rename package as package name of debian jessie
DEBIANNAME_${PN}-dev = "libustr-dev"

BBCLASSEXTEND = "native"
