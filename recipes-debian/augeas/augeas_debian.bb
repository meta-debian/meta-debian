SUMMARY = "Augeas configuration API"
DESCRIPTION = "Augeas is a configuration editing tool. It parses configuration files in their \
native formats and transforms them into a tree. Configuration changes are made \
by manipulating this tree and saving it back into native config files."
HOMEPAGE = "http://augeas.net/"

inherit debian-package
PV = "1.2.0"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=bbb461211a33b134d42ed5ee802b37ff"

SRC_URI += "file://add-missing-argz-conditional.patch"

inherit autotools-brokensep

DEPENDS += "libxml2 readline"

do_install_append() {
	rm -rf ${D}${bindir}/fadot \
	       ${D}${libdir}/*.la
}
PACKAGES =+ "${PN}-lenses ${PN}-tools lib${PN}-dev lib${PN}"

FILES_${PN}-lenses = "${datadir}/augeas/*"
FILES_${PN}-tools = "${bindir}/*"
FILES_lib${PN}-dev = "${includedir}/* ${libdir}/*.so ${libdir}/pkgconfig/*"
FILES_lib${PN} = "${libdir}/*${SOLIBS}"
FILES_${PN}-doc += "${datadir}/vim/*"

PKG_lib${PN} = "lib${PN}0"
RPROVIDES_${PN} += "lib${PN}0"

RDEPENDS_lib${PN} += "${PN}-lenses"

BBCLASSEXTEND = "native"
