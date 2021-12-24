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
	rel_lib_prefix=`echo ${libdir} | sed 's,\(^/\|\)[^/][^/]*,..,g'`
	ln -sf ${rel_lib_prefix}${base_libdir}/libusb-0.1.so.4 \
		${D}${libdir}/libusb-0.1.so.4

	LINKLIB=$(basename $(readlink ${D}${libdir}/libusb.so))
	ln -sf ${rel_lib_prefix}${base_libdir}/$LINKLIB ${D}${libdir}/libusb.so

	rm ${D}${libdir}/*.la
}

PACKAGES =+ "${DPN}++ ${DPN}++-dev"

FILES_${PN} += "${bindir}/libusb-config"
FILES_${DPN}++ = "${libdir}/libusbpp-*"
FILES_${DPN}++-dev = "${includedir}/usbpp.h ${libdir}/libusbpp.so"

DEBIANNAME_${PN} = "${DPN}-0.1-4"
DEBIANNAME_${DPN}++ = "${DPN}++-0.1-4c2"
DEBIANNAME_${DPN}++-dev = "${DPN}++-dev"
