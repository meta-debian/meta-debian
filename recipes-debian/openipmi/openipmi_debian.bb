PR = "r0"

inherit debian-package

LICENSE = "GPLv2+ & LGPLv2.1+ & BSD"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
	file://COPYING.LIB;md5=d8045f3b8f929c1cb29a1e3fd737b499 \
	file://COPYING.BSD;md5=4b318d4160eb69c8ee53452feb1b4cdf \
"

DEPENDS = "popt ncurses"

DEBIAN_PATCH_TYPE = "nopatch"

inherit autotools

# Configure follow debian/rules
# remove unrecognised options: --disable-rpath
EXTRA_OECONF = "--without-openssl"

PARALLEL_MAKEINST = ""

PACKAGES =+ "lib${PN}"
FILES_lib${PN} = "${libdir}/*${SOLIBS}"

LEAD_SONAME = "libOpenIPMI.so"
DEBIANNAME_${PN}-dev = "lib${PN}-dev"
