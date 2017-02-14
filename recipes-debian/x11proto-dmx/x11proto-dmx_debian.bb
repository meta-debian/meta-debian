SUMMARY = "X11 Distributed Multihead X extension wire protocol"
DESCRIPTION = "\
This package provides development headers describing the wire protocol \
for the DMX extension, used to set up a distributed multi-head environment \
with a single server acting as a gateway to multiple X servers on multiple \
machines. \
"
PR = "r0"
inherit debian-package
PV = "2.3.1"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=a3c3499231a8035efd0e004cfbd3b72a"
inherit autotools
DEPENDS += "util-macros"

# Debian's source code isn't contains patch file
DEBIAN_PATCH_TYPE = "nopatch"

#follow debian/x11proto-dmx-dev.install
do_install_append() {
	install -d ${D}${datadir}/pkgconfig
	mv  ${D}${libdir}/pkgconfig/* ${D}${datadir}/pkgconfig/
	rm -r ${D}${libdir}
}
FILES_${PN}-dev += "${datadir}/pkgconfig"

# ${PN} is empty so we need to tweak -dev and -dbg package dependencies
RDEPENDS_${PN}-dev = ""
RRECOMMENDS_${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"
