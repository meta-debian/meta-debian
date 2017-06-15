SUMMARY = "Little CMS 2 color management library"
DESCRIPTION = "LittleCMS 2 intends to be a small-footprint color management engine, with \
special focus on accuracy and performance. It uses the International Color \
Consortium standard (ICC) of color management. LittleCMS 2 is a full \
implementation of ICC specification 4.2 plus all addendums. It fully supports \
all V2 and V4 profiles, including abstract, devicelink and named color \
profiles."
HOMEPAGE = "http://www.littlecms.com/"

inherit debian-package
PV = "2.6"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=6c786c3b7a4afbd3c990f1b81261d516"

inherit autotools

DEPENDS += "tiff libjpeg-turbo zlib"
PACKAGES =+ "${PN}-utils"

FILES_${PN}-utils = "${bindir}/*"

RPROVIDES_${PN} += "liblcms2-2"
RPROVIDES_${PN}-dev += "liblcms2-dev"
RPROVIDES_${PN}-utils += "liblcms2-utils"
