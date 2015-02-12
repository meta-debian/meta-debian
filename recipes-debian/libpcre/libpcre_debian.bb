require recipes-support/libpcre/libpcre_8.34.bb

FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-support/libpcre/libpcre:\
"

inherit debian-package
BPN = "pcre3"
DEBIAN_SECTION = "libs"
DPR = "0"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENCE;md5=ded617e975f28e15952dc68b84a7ac1a"

#No need to apply the following patch because of difference source code version.
#-fix-pcre-name-collision.patch
SRC_URI += "\
file://pcre-cross.patch \
file://run-ptest \
file://Makefile \
"
