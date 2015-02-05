require recipes-core/libxml/libxml2_2.9.1.bb
FILESEXTRAPATHS_prepend ="\
${COREBASE}/meta/recipes-core/libxml/libxml2:\
"

inherit debian-package
DEBIAN_SECTION = "libs"
DPR = "0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
	file://Copyright;md5=2044417e2e5006b65a8b9067b683fcf1 \
	file://hash.c;beginline=6;endline=15;md5=96f7296605eae807670fb08947829969 \
	file://list.c;beginline=4;endline=13;md5=cdbfa3dee51c099edb04e39f762ee907 \
	file://trio.c;beginline=5;endline=14;md5=6c025753c86d958722ec76e94cae932e \
"

SRC_URI += "\
	file://libxml-64bit.patch \
	file://ansidecl.patch \
	file://runtest.patch \
	file://run-ptest \
	file://libxml2-CVE-2014-0191-fix.patch \
	file://python-sitepackages-dir.patch \
"

# Patch debian/patches/0002-fix-python-multiarch-includes.patch
# cause error "Python.h: No such file or directory"
# Need to restore the compile flag that this patch has modified
SRC_URI += " \
	file://restore-python-include-flag.patch \
"
