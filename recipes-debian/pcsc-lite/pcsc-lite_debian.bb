SUMMARY = "Middleware to access a smart card using SCard API"
DESCRIPTION = "Middleware to access a smart card using PC/SC."
HOMEPAGE = "https://pcsclite.apdu.fr/"
LICENSE = "BSD-3-Clause & GPLv3+ & ISC"

LIC_FILES_CHKSUM = "file://COPYING;md5=628c01ba985ecfa21677f5ee2d5202f6 \
					file://src/spy/libpcscspy.c;beginline=1;endline=18;md5=e90b06254fc7c11b26cc29c9e0fbcd31 \
					file://src/simclist.c;beginline=1;endline=16;md5=f4b1753c369c1f7da78ebf0b30546b04"

inherit debian-package
require recipes-debian/sources/pcsc-lite.inc

DEPENDS = "udev"

inherit autotools pkgconfig

EXTRA_OECONF += "--disable-libsystemd \
				 --enable-libudev \
				 --enable-usbdropdir=${libdir}/pcsc/drivers \
				 "

RDEPENDS_${PN} += "python"
