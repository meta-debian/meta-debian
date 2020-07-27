SUMMARY = "Utility for retrieving and analyzing function tracing in the kernel"
HOMEPAGE = "http://kernelshark.org/"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

inherit debian-package
require recipes-debian/sources/trace-cmd.inc
DEBIAN_UNPACK_DIR = "${WORKDIR}/${BPN}-v${PV}"

EXTRA_OEMAKE = " \
	'prefix=${prefix}' 'plugin_dir=${libdir}/${BPN}/plugins' \
	SWIG_DEFINED=0 NO_PYTHON=1 V=1 \
"

do_compile() {
	oe_runmake
}

do_install() {
	oe_runmake DESTDIR="${D}" install
}
