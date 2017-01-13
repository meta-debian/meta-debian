#
# base recipe: meta/recipes-extended/bc/bc_1.06.bb
# base branch: daisy
#

SUMMARY = "GNU bc arbitrary precision calculator language"
DESCRIPTION = "GNU bc is an interactive algebraic language with arbitrary precision which \
follows the POSIX 1003.2 draft standard, with several extensions including \
multi-character variable names, an `else' statement and full Boolean \
expressions.  GNU bc does not require the separate GNU dc program."
HOMEPAGE = "http://ftp.gnu.org/gnu/bc/"

PR = "r1"

inherit debian-package
PV = "1.06.95"

LICENSE = "GPLv2+ & LGPLv2.1+ & MIT"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=b492e6ce406929d0b0a96c4ae7abcccf \
	file://COPYING.LIB;md5=bf0962157c971350d4701853721970b4 \
	file://install-sh;beginline=6;endline=32;md5=2ab67672a6ca4781a8291d8e11f5ccaf \
	file://bc/bcdefs.h;endline=28;md5=c130bad80e7e25940b5dd478a4cf9498 \
	file://dc/dc.h;endline=23;md5=8b73f8cca832dac936fae39eea4269e0 \
	file://lib/number.c;endline=30;md5=844a960b70c05456062f4d53b20f67b0 \
"

DEPENDS = "flex-native"

inherit autotools-brokensep

PACKAGES += "dc"
FILES_dc = "${bindir}/dc"
FILES_${PN} = "${bindir}/bc"

BBCLASSEXTEND = "native nativesdk"
