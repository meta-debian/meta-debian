SUMMARY = "library for GIF images"
DESCRIPTION = "GIFLIB is a package of portable tools and \
library routines for working with GIF images."
HOMEPAGE = "http://giflib.sourceforge.net/"

PR = "r0"

inherit debian-package
PV = "4.1.6"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=ae11c61b04b2917be39b11f78d71519a"

inherit autotools

EXTRA_OECONF = "--disable-x11"

do_install_append() {
	# Follow debian/libgif-dev.links
	ln -s libgif.a        ${D}${libdir}/libungif.a
	ln -s libgif.la       ${D}${libdir}/libungif.la
	ln -s libgif.so.4.1.6 ${D}${libdir}/libungif.so
}

PACKAGES =+ "libgif"

FILES_libgif = "${libdir}/libgif${SOLIBS}"
RPROVIDES_libgif = "libgif4"

RDEPENDS_${PN} = "perl"
DEBIANNAME_${PN} = "${PN}-tools"
RPROVIDES_${PN} = "${PN}-tools libungif-bin"

DEBIANNAME_${PN}-dev = "libgif-dev"
RPROVIDES_${PN}-dev = "libgif-dev"

BBCLASSEXTEND = "native"
