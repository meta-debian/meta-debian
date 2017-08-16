PR = "r0"

inherit debian-package allarch
PV = "1.5.56+deb8u1"

SUMMARY = "Debian configuration management system"
DESCRIPTION = "\
 Debconf is a configuration management system for debian packages. Packages\
 use Debconf to ask questions when they are installed.\
"
LICENSE = "BSD-2-Clause"
SECTION = "admin"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=1aa3b78d3182195ba56c01cb7227ead3"

DEBIAN_PATCH_TYPE = "nopatch"

do_configure() {
	:
}

do_compile() {
	:
}

do_install() {
	prefix="${D}" oe_runmake install-utils
	prefix="${D}" oe_runmake install-rest
}

do_install_class-native () {
	prefix="${D}${STAGING_DIR_NATIVE}" oe_runmake install
}

PACKAGES =+ "${PN}-utils"
FILES_${PN}-utils = " \
${bindir}/debconf-get-selections \
${bindir}/debconf-getlang \
${bindir}/debconf-loadtemplate \
${bindir}/debconf-mergetemplate \
"
FILES_${PN} += "${datadir} ${libdir}"

BBCLASSEXTEND = "native"
