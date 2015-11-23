DESCRIPTION = "SWIG - Simplified Wrapper and Interface Generator"

PR = "r0"

inherit debian-package

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = " \
file://LICENSE;md5=e7807a6282784a7dde4c846626b08fc6 \
"

DEPENDS = "python libpcre"
EXTRA_OECONF += " --program-suffix=2.0"

inherit autotools

BBCLASSEXTEND = "native"

do_configure_prepend() {
	sed -i "s:swig/\${PACKAGE_VERSION}:swig2.0:" ${S}/configure.ac
}

do_configure() {
	oe_runconf
}
do_install_append() {
	ln -sf ccache-swig2.0 ${D}${bindir}/ccache-swig
	ln -sf swig2.0 ${D}${bindir}/swig
}
DPN = "swig2.0"
PACKAGES += "swig2.0"
FILES_swig2.0 = "${datadir} ${bindir}/swig2.0 ${bindir}/ccache-swig2.0"
FILES_${PN} = "${bindir}/ccache-swig ${bindir}/swig"
