SUMMARY = "OpenBIOS FCode utilities"
HOMEPAGE = "http://www.openbios.org"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=6f96a62bfc31d872bee045fc9ca3564f"

inherit debian-package
require recipes-debian/sources/fcode-utils.inc
DEBIAN_UNPACK_DIR = "${WORKDIR}/${BPN}"

do_compile() {
	oe_runmake
}

do_install() {
	install -d ${D}${bindir} \
	           ${D}${datadir}/${BPN}/localvalues
	install -m 0755 ${B}/toke/toke ${D}${bindir}/
	install -m 0755 ${B}/detok/detok ${D}${bindir}/
	install -m 0755 ${B}/romheaders/romheaders ${D}${bindir}/
	install -m 0644 ${B}/localvalues/* ${D}${datadir}/${BPN}/localvalues/
}

BBCLASSEXTEND = "native"
