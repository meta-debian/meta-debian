SUMMARY = "lightweight messaging kernel"
DESCRIPTION = "\
	ØMQ is a library which extends the standard socket interfaces with features \
	traditionally provided by specialised messaging middleware products. \
	ØMQ sockets provide an abstraction of asynchronous message queues, multiple \
	messaging patterns, message filtering (subscriptions), seamless access 	\
	to multiple transport protocols and more."
HOMEPAGE = "http://www.zeromq.org"

PR = "r0"
inherit debian-package pkgconfig
PV = "4.0.5+dfsg"

LICENSE = "GPLv3+ & LGPLv3+ & MIT"
LIC_FILES_CHKSUM = "\
		file://COPYING;md5=f7b40df666d41e6508d03e1c207d498f \
		file://COPYING.LESSER;md5=d5311495d952062e0e4fbba39cbf3de1 \
		file://debian/zmq.hpp;md5=388ddb07fdd88b040769e8f4c9b397fa \
		"
EXTRA_OECONF += "--with-system-pgm"
DEPENDS += "libpgm"
inherit autotools
#install follow Debian jessie
do_install_append() {
	rm -r ${D}${bindir}
	rm ${D}${libdir}/libzmq.la
	install -m 0644 ${S}/debian/zmq.hpp ${D}${includedir}/
}
PKG_${PN}-dbg = "libzmq3-dbg"
PKG_${PN}-dev = "libzmq3-dev"
