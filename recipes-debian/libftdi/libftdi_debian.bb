SUMMARY = "Library to control and program the FTDI USB controller"
DESCRIPTION = "\
	This library could talk to FTDI's FT232 and FT245 type USB chips from \
	userspace. It uses libusb to communicate with the chips. \
	Functionalities include the possibility to use the chips in standard \
	mode, in bitbang mode, and to read or write the serial EEPROM. \
"
HOMEPAGE = "http://www.intra2net.com/en/developer/libftdi/"
PR = "r0"
inherit debian-package
PV = "0.20"

LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "\
	file://LICENSE;md5=2e20d74de059b32006dc58fafdfa59b0 \
	file://COPYING.LIB;md5=db979804f025cf55aabec7129cb671ed \
	file://COPYING.GPL;md5=751419260aa954499f7abaabaa882bbe"
inherit autotools-brokensep pkgconfig

DEPENDS += "libusb boost swig-native"

#follow debian/rules
ETRA_OECONF += "--enable-libftdipp --disable-python-binding"

do_install_append() {
	#remove the unwanted files
	for file in ${D}${bindir}/*
	do
		[ $file != ${D}${bindir}/libftdi-config ] && rm $file
	done
	rm ${D}${libdir}/*.la
}
PACKAGES =+ "libftdipp libftdipp-dev"

FILES_libftdipp = "${libdir}/libftdipp.so.*"
FILES_libftdipp-dev = "${includedir}/ftdi.hpp ${libdir}/pkgconfig/libftdipp.pc \
			${libdir}/libftdipp.so"
FILES_${PN}-dev += "${bindir}/${DPN}-config"

PKG_${PN} = "${PN}1"
PKG_${PN}-dbg = "${PN}1-dbg"
