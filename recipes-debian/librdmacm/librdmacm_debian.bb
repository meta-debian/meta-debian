SUMMARY = "Library for managing RDMA connections"
DESCRIPTION = "librdmacm is a library that allows applications to set up reliable\n\
 connected and unreliable datagram transfers when using RDMA adapters.\n\
 It provides a transport-neutral interface in the sense that the same\n\
 code can be used for both InfiniBand and iWARP adapters.  The\n\
 interface is based on sockets, but adapted for queue pair (QP) based\n\
 semantics: communication must use a specific RDMA device, and data\n\
 transfers are message-based.\n\
 .\n\
 librdmacm only provides communication management (connection setup\n\
 and tear-down) and works in conjunction with the verbs interface\n\
 provided by libibverbs, which provides the interface used to actually\n\
 transfer data."
HOMEPAGE = "https://www.openfabrics.org/downloads/rdmacm/"

inherit debian-package
PV = "1.0.19.1"

LICENSE = "GPLv2+ | BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=39cc3044d68741f9005da73e9b92db95"

inherit autotools

#Empty DEBIAN_QUILT_PATCHES to avoid error "debian/patches not found"
DEBIAN_QUILT_PATCHES = ""

DEPENDS += "libibverbs"

do_install_append() {
	# Remove unwanted files
	rm -rf ${D}${libdir}/rsocket/librspreload.a \
	       ${D}${libdir}/rsocket/librspreload.la \
	       ${D}${libdir}/librdmacm.la
}
PACKAGES =+"rdmacm-utils"

FILES_rdmacm-utils = "${bindir}/*"
FILES_${PN} += "${libdir}/rsocket/librspreload${SOLIBS}"
FILES_${PN}-dev += "${libdir}/rsocket/librspreload.so"
