#
# Base recipe: meta/recipes-support/libusb/libusb1_1.0.9.bb
# Base branch: daisy
# Base commit: 9e4aad97c3b4395edeb9dc44bfad1092cdf30a47
#

SUMMARY = "Userspace library to access USB (version 1.0)"
HOMEPAGE = "http://libusb.sf.net"
BUGTRACKER = "http://www.libusb.org/report"

inherit debian-package autotools pkgconfig
PV = "1.0.19"

PR = "r0"
DPN = "libusb-1.0"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

# Don't configure udev by default since it will cause a circular
# dependecy with udev package, which depends on libusb
EXTRA_OECONF = "--libdir=${base_libdir} --disable-udev"

do_install_append() {
	install -d ${D}${libdir}
	if [ ! ${D}${libdir} -ef ${D}${base_libdir} ]; then
		mv ${D}${base_libdir}/pkgconfig ${D}${libdir}
	fi
}

FILES_${PN} += "${base_libdir}/*.so.*"

# Move .a library to dev package
FILES_${PN}-dev += "${base_libdir}/*.so ${base_libdir}/*.la"

BBCLASSEXTEND = "native nativesdk"
