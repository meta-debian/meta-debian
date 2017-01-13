DESCRIPTION = "SWIG - Simplified Wrapper and Interface Generator"

PR = "r1"

inherit debian-package
PV = "2.0.12"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = " \
file://LICENSE;md5=e7807a6282784a7dde4c846626b08fc6 \
"

DEPENDS = "python libpcre tcl"

# Path to find tclConfig.sh
EXTRA_OECONF_class-target = " --with-tclconfig=${STAGING_BINDIR_CROSS}"
EXTRA_OECONF_class-native = " --with-tclconfig=${STAGING_LIBDIR_NATIVE}/tcl8.6"

EXTRA_OECONF_append = " \
    --with-swiglibdir=${datadir}/swig2.0 \
    --without-mzscheme \
    --program-suffix=2.0 \
"

# Currently, meta-debian doesn't provide ruby and pike,
# disable them to avoid swig looking for them in host system
EXTRA_OECONF_append = " \
    --without-ruby \
    --without-pike \
"

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
