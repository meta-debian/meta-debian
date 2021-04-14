SUMMARY = "Library for parsing configuration files"
DESCRIPTION = "libConfuse is a configuration file parser library written in C. It \
 supports sections and (lists of) values (strings, integers, floats, \
 booleans or other sections), as well as some other features (such as \
 single/double-quoted strings, environment variable expansion, \
 functions and nested include statements)."
HOMEPAGE = "http://www.nongnu.org/confuse/"

LICENSE = "ISC"
inherit debian-package
PV = "2.7"
PR = "r0"
LIC_FILES_CHKSUM = "file://src/confuse.c;beginline=2;endline=14;md5=c1af423e2b67c0bb651f7c4ec5842e9e"

inherit autotools binconfig pkgconfig lib_package gettext

EXTRA_OECONF = "--enable-shared --enable-static --disable-silent-rules"

BBCLASSEXTEND = "native"
