SUMMARY = "Library for parsing configuration files"
DESCRIPTION = "libConfuse is a configuration file parser library written in C. It \
 supports sections and (lists of) values (strings, integers, floats, \
 booleans or other sections), as well as some other features (such as \
 single/double-quoted strings, environment variable expansion, \
 functions and nested include statements)."
HOMEPAGE = "http://www.nongnu.org/confuse/"

LICENSE = "GPLv2+ & GPLv3+"
inherit debian-package
PV = "2.7"
PR = "r0"
LIC_FILES_CHKSUM = "file://support/missing;beginline=6;endline=25;md5=40b23661ee5b2d5c1ec9e09e5e928e56 \
                    file://support/config.guess;beginline=7;endline=25;md5=e443b5b496d01ee52219d5269560d697"

inherit autotools binconfig pkgconfig lib_package gettext

EXTRA_OECONF = "--enable-shared --enable-static --disable-silent-rules"

BBCLASSEXTEND = "native"
