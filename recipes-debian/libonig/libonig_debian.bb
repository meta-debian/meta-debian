SUMMARY = "Oniguruma regular expressions library"
DESCRIPTION = "The characteristics of this library is that different \
character encoding for every regular expression object \
can be specified."
HOMEPAGE = "http://www.geocities.jp/kosako3/oniguruma/"

PR = "r0"

inherit debian-package
PV = "5.9.5"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=0d4861b5bc0c392a5aa90d9d76ebd86f"

PROVIDES += "onig"

# Base on http://cgit.openembedded.org/meta-openembedded/tree/meta-oe/recipes-support/onig
# do-not-use-system-headers.patch:
#	Don't use headers from host system.
SRC_URI += "file://do-not-use-system-headers.patch"

inherit autotools binconfig

DEBIANNAME_${PN}-dbg = "${PN}2-dbg"

BBCLASSEXTEND = "native"
