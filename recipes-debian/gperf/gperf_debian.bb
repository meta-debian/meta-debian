DESCRIPTION = "GNU gperf is a perfect hash function generator"
HOMEPAGE = "http://www.gnu.org/software/gperf"
SUMMARY  = "Generate a perfect hash function from a set of keywords"
LICENSE  = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
                    file://src/main.cc;beginline=8;endline=19;md5=dec8f611845d047387ed56b5b85fa99b"

inherit debian-package
require recipes-debian/sources/gperf.inc

inherit autotools

# The nested configures don't find the parent aclocal.m4 out of the box, so tell
# it where to look explicitly (mirroring the behaviour of upstream's Makefile.devel).
EXTRA_AUTORECONF += " -I ${S} --exclude=aclocal"

BBCLASSEXTEND = "native"
