require recipes-devtools/automake/automake_1.14.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-devtools/automake/automake:\
"

DPN = "automake-1.14"
inherit debian-package
DEBIAN_SECTION = "devel"

DPR = "0"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

# for native package
SRC_URI += " \
file://python-libdir.patch \
file://py-compile-compile-only-optimized-byte-code.patch \
file://buildtest.patch \
"

# regenerate dependent files created by aclocal and automake
do_configure_prepend() {
	cd ${S}
	./bootstrap.sh && cd -
}
