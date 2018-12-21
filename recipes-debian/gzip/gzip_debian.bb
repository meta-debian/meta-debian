require recipes-extended/gzip/gzip.inc

inherit debian-package
require recipes-debian/sources/gzip.inc

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
	file://gzip.h;beginline=8;endline=20;md5=6e47caaa630e0c8bf9f1bc8d94a8ed0e \
"

FILESPATH_append = ":${COREBASE}/meta/recipes-extended/gzip/gzip-1.9"
SRC_URI += "file://gnulib.patch"

PROVIDES_append_class-native = " gzip-replacement-native"

BBCLASSEXTEND = "native"
