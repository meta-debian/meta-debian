#
# base recipe: meta/recipes-devtools/m4/m4.inc
# base branch: master
# base commit: dbda297fd91aab2727f7a69d3b7d3a32ad4261d2
#

SUMMARY = "Traditional Unix macro processor"
HOMEPAGE = "https://www.gnu.org/software/m4/m4.html"

inherit debian-package
require recipes-debian/sources/m4.inc

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
	file://examples/COPYING;md5=4031593b2166d6c47cae282d944a7ede \
"

inherit autotools texinfo

# Fix "Argument list too long" error when len(TMPDIR) = 410
acpaths = "-I ./m4"

EXTRA_OECONF += "--without-libsigsegv-prefix"

EXTRA_OEMAKE += "'infodir=${infodir}'"
