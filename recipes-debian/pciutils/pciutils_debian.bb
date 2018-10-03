#
# base recipe: meta/recipes-bsp/pciutils/pciutils_3.5.6.bb
# base branch: master
# base commit: 5fef9fcd58e88f296606ae6780904345014e4d29
#
SUMMARY = "PCI utilities"
DESCRIPTION = 'The PCI Utilities package contains a library for portable access \
to PCI bus configuration space and several utilities based on this library.'
HOMEPAGE = "http://atrey.karlin.mff.cuni.cz/~mj/pciutils.shtml"

DEPENDS = "zlib kmod"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

inherit debian-package
require recipes-debian/sources/pciutils.inc

SRC_URI += "\
    file://Makefile_remove_udeb.patch \
    file://configure.patch \
"

inherit multilib_header pkgconfig

PCI_CONF_FLAG = "ZLIB=yes DNS=yes SHARED=yes STRIP= LIBDIR=${libdir}"

# see configure.patch
do_configure () {
	(
	  cd lib && \
	  ${PCI_CONF_FLAG} ./configure ${PV} ${datadir} ${TARGET_OS} ${TARGET_ARCH}
	)
}

export PREFIX = "${prefix}"
export SBINDIR = "${sbindir}"
export SHAREDIR = "${datadir}"
export MANDIR = "${mandir}"

EXTRA_OEMAKE = "-e MAKEFLAGS= ${PCI_CONF_FLAG}"

# The configure script breaks if the HOST variable is set
HOST[unexport] = "1"

do_install () {
	oe_runmake DESTDIR=${D} install install-lib

	install -d ${D}${bindir}
	ln -sf ${@oe.path.relative("${bindir}","${sbindir}")}/lspci ${D}${bindir}/lspci

	oe_multilib_header pci/config.h
}

PACKAGES =+ "${PN}-ids libpci"
FILES_${PN}-ids = "${datadir}/pci.ids*"
FILES_libpci = "${libdir}/libpci.so.*"
SUMMARY_${PN}-ids = "PCI utilities - device ID database"
DESCRIPTION_${PN}-ids = "Package providing the PCI device ID database for pciutils."
RDEPENDS_${PN} += "${PN}-ids"
