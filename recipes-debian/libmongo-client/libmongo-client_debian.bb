SUMMARY = "alternative C driver for MongoDB,"
DESCRIPTION = "\
	libmongo-client is meant to be a stable (API, ABI and quality alike), \
	clean, well documented and well tested shared library, that strives to\
	make the most common use cases as convenient as possible"
HOMEPAGE = "https://github.com/algernon/libmongo-client"

PR = "r0"
inherit debian-package
PV = "0.1.8"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

inherit autotools pkgconfig
DEPENDS += "glib-2.0"

#install follow Debian jessie
do_install_append() {
	rm ${D}${libdir}/libmongo-client.la
	LINKLIB=$(basename $(readlink ${D}${libdir}/libmongo-client.so))
	chmod 0644 ${D}${libdir}/${LINKLIB}
}
#correct the sub-package name
DEBIANNAME_${PN}-dbg = "${PN}0-dbg"
