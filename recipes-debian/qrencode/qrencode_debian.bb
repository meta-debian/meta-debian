SUMMARY = "QR Code encoder into PNG image"
DESCRIPTION = "Qrencode is a utility software using libqrencode to encode string data in \
 a QR Code and save as a PNG or an EPS image."
HOMEPAGE = "http://megaui.net/fukuchi/works/qrencode/index.en.html"

inherit debian-package
PV = "3.4.3"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=2d5025d4aa3495befef8f17206a5b0a1"

DEPENDS += "libsdl libpng"
inherit autotools

PACKAGES =+ "lib${PN}"

FILES_lib${PN} = "${libdir}/libqrencode${SOLIBS}"

DEBIANNAME_${PN}-dev = "lib${PN}-dev"
RPROVIDES_${PN}-dev = "lib${PN}-dev"
RPROVIDES_lib${PN} = "lib${PN}3"
