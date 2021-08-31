SUMMARY = "C preprocessor interface to the make utility"
DESCRIPTION = "Imake is used to generate Makefiles from a template, a \
set of cpp macro functions, and a per-directory input file called an \
Imakefile. This allows machine dependencies (such as compiler options, \
alternate command names, and special make rules) to be kept separate \
from the descriptions of the various items to be built."

require xorg-util-common.inc

PR = "${INC_PR}.0"

LIC_FILES_CHKSUM = "file://COPYING;md5=b9c6cfb044c6d0ff899eaafe4c729367"

DEPENDS = "util-macros xproto xorg-cf-files"

RDEPENDS_${PN} = "perl xproto"
