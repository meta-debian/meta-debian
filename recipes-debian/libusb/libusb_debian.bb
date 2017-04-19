SUMMARY = "userspace USB programming library"
DESCRIPTION = "\
	Library for programming USB applications without the knowledge \
	of Linux kernel internals."
HOMEPAGE = "http://www.linux-usb.org/"

inherit debian-package autotools pkgconfig
PV = "0.1.12"

PR = "r0"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=dcf3c825659e82539645da41a7908589"
EXTRA_OECONF = "--disable-build-docs"

do_install_append() {
	install -d ${D}${base_libdir}
	mv ${D}${libdir}/libusb-0.1.so.* ${D}${base_libdir}/
	
	ln -sf ../../lib/libusb-0.1.so.4 \
		${D}${libdir}/libusb-0.1.so.4

	LINKLIB=$(basename $(readlink ${D}${libdir}/libusb.so))
	ln -sf ../../lib/$LINKLIB ${D}${libdir}/libusb.so

	rm ${D}${libdir}/*.la
}

PACKAGES =+ "${PN}++ ${PN}++-dev"

FILES_${PN} += "${bindir}/libusb-config"
FILES_${PN}++ = "${libdir}/libusbpp-*"
FILES_${PN}++-dev = "${includedir}/usbpp.h ${libdir}/libusbpp.so"

DEBIANNAME_${PN} = "${PN}-0.1-4"
DEBIANNAME_${PN}++ = "${PN}++-0.1-4c2"
DEBIANNAME_${PN}++-dev = "${PN}++-dev"
