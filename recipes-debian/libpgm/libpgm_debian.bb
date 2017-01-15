SUMMARY = "OpenPGM shared library"
DESCRIPTION = "\
	OpenPGM is an open source implementation of the Pragmatic General Multicast \
	(PGM) specification in RFC 3208 available at www.ietf.org. PGM is a reliable\
	and scalable multicast protocol that enables receivers to detect loss,\
	request retransmission of lost data, or notify an application of\
	unrecoverable loss. PGM is a receiver-reliable protocol, which means the\
	receiver is responsible for ensuring all data is received, absolving the\
	sender of reception responsibility. PGM runs over a best effort datagram\
	service, currently OpenPGM uses IP multicast but could be implemented above\
	switched fabrics such as InfiniBand.\
	"
HOMEPAGE = "http://code.google.com/p/openpgm/"
PR = "r0"
inherit debian-package
PV = "5.1.118-1~dfsg"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=fbc093901857fcd118f065f900982c24"

#configure-fix-cross-compiling_debian.patch:
#	this patch enables to configure the package when cross-compiling in a way
#	recommended by Autoconf manual
SRC_URI += "file://configure-fix-cross-compiling_debian.patch"

inherit autotools-brokensep
S = "${DEBIAN_UNPACK_DIR}/openpgm/pgm"

EXTRA_OECONF += "\
	ac_cv_file__proc_cpuinfo=yes \
	ac_cv_file__dev_rtc=no \
	ac_cv_file__dev_hpet=no \
	ac_fn_c_try_run=no \
	pgm_unaligned_pointers=no"

#install follow Debian jessie
do_install_append() {
	ln -s libpgm-5.1.so.0 ${D}${libdir}/libpgm-5.1.so
}
DEBIANNAME_${PN}-dev = "libpgm-dev"
DEBIANNAME_${PN}-dbg = "libpgm-dbg"
DEBIANNAME_${PN}-staticdev = "libpgm-staticdev"
