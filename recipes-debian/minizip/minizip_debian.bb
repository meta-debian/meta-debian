SUMMARY = "compression library"
DESCRIPTION = "minizip is a minimalistic library that supports compressing, extracting, \
 viewing, and manipulating zip files."
HOMEPAGE = "http://www.winimage.com/zLibDll/minizip.html"

inherit debian-package
PV = "1.1"

LICENSE = "Zlib"
LIC_FILES_CHKSUM = " \
	file://zip.h;beginline=14;endline=30;md5=8eaa8535a3a1a2296b303f40f75385e7 \
"

inherit autotools

DEPENDS += "zlib"

do_compile() {
	oe_runmake minizip miniunzip
}

do_install_append() {
	install -d ${D}${bindir}
	install -m 0755 ${B}/.libs/minizip ${D}${bindir}
	install -m 0755 ${B}/.libs/miniunzip ${D}${bindir}
}

PACKAGES =+ "lib${DPN}"

FILES_lib${DPN} = "${libdir}/lib*${SOLIBS}"

DEBIANNAME_${PN}-dev = "lib${DPN}-dev"

RPROVIDES_lib${DPN} += "lib${DPN}1"
RPROVIDES_${PN}-dev += "lib${DPN}-dev"
