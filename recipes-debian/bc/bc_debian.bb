#
# base recipe: meta/recipes-extended/bc/bc_1.06.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package

LICENSE = "GPLv2+ & LGPLv2.1+ & MIT"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=b492e6ce406929d0b0a96c4ae7abcccf \
	file://COPYING.LIB;md5=bf0962157c971350d4701853721970b4 \
	file://install-sh;beginline=6;endline=32;md5=2ab67672a6ca4781a8291d8e11f5ccaf \
	file://bc/bcdefs.h;endline=28;md5=c130bad80e7e25940b5dd478a4cf9498 \
	file://dc/dc.h;endline=23;md5=8b73f8cca832dac936fae39eea4269e0 \
	file://lib/number.c;endline=30;md5=844a960b70c05456062f4d53b20f67b0 \
"

DEPENDS = "flex"

inherit autotools-brokensep

PACKAGES += "dc"
FILES_dc = "${bindir}/dc"
FILES_${PN} = "${bindir}/bc"

BBCLASSEXTEND = "native nativesdk"
