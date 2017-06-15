SUMMARY = "ser-space access to Linux Kernel SCTP"
DESCRIPTION = "SCTP (Stream Control Transmission Protocol) is a message oriented, \
reliable transport protocol, with congestion control, support for \
transparent multi-homing, and multiple ordered streams of messages. \
RFC2960 defines the core protocol."
HOMEPAGE = "http://lksctp.sf.net/"

inherit debian-package
PV = "1.0.16+dfsg"

LICENSE = "GPL-2+ & LGPL-2.1+ & BSD-3-Clause"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=0c56db0143f4f80c369ee3af7425af6e \
	file://COPYING.lib;md5=0a1b79af951c42a9c8573533fbba9a92 \
	file://src/withsctp/sctp_bind.c;beginline=10;endline=37;md5=4f5ceff92458066c8cfdfe5b3f824aa0"

inherit autotools

do_install_append() {
	# follow libsctp-dev.install
	install -d ${D}${docdir}/libsctp-dev/examples/
	mv ${D}${datadir}/lksctp-tools/* ${D}${docdir}/libsctp-dev/examples/

	rm -rf ${D}${libdir}/lksctp-tools/libwithsctp.so \
	       ${D}${libdir}/lksctp-tools/libwithsctp.la \
	       ${D}${libdir}/lksctp-tools/libwithsctp.a
}

PACKAGES =+ "libsctp libsctp-dev"

FILES_libsctp = "${libdir}/libsctp${SOLIBS}"
FILES_libsctp-dev = "${includedir}/* \
                     ${libdir}/*.so \
                     ${libdir}/*.la \
                     "
RPROVIDES_libsctp = "libsctp1"
