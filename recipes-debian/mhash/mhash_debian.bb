SUMMARY = "Library for cryptographic hashing and message authentication"
DESCRIPTION = "\
	Library for cryptographic hashing and message authentication Mhash is \
	a library that provides a uniform interface to a large number of hash \
	algorithms.  These algorithms can be used to compute checksums, message\
	digests, and other signatures.  The HMAC support implements the basics \
	for message authentication, following RFC 2104. Mhash also provides \
	several key-generation algorithms, including those of OpenPGP (RFC 2440)"
HOMEPAGE = "http://mhash.sourceforge.net/"

PR = "r0"
inherit debian-package
PV = "0.9.9.9"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7"

inherit autotools-brokensep pkgconfig

#config follow debian/rules
EXTRA_OECONF += "--enable-pkgconfig"

#install follow Debian jessie
do_install_append() {
	rm ${D}${libdir}/libmhash.la
	LINKLIB=$(basename $(readlink ${D}${libdir}/libmhash.so))
	chmod 0644 ${D}${libdir}/${LINKLIB}
}

