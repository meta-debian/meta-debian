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
PACKAGES =+ "${PN}-lenses ${PN}-tools lib${DPN}-dev lib${DPN}"

FILES_${PN}-lenses = "${datadir}/augeas/*"
FILES_${PN}-tools = "${bindir}/*"
FILES_lib${DPN}-dev = "${includedir}/* ${libdir}/*.so ${libdir}/pkgconfig/*"
FILES_lib${DPN} = "${libdir}/*${SOLIBS}"
FILES_${PN}-doc += "${datadir}/vim/*"

PKG_lib${DPN} = "lib${DPN}0"
RPROVIDES_${PN} += "lib${DPN}0"

RDEPENDS_lib${DPN} += "${PN}-lenses"

BBCLASSEXTEND = "native"
