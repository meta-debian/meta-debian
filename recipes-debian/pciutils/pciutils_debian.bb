#
# Base recipe: meta/recipes-bsp/pciutils/pciutils_3.2.1.bb
# Base branch: daisy
# Base commit: 9e4aad97c3b4395edeb9dc44bfad1092cdf30a47
#
SUMMARY = "PCI utilities"
DESCRIPTION = 'The PCI Utilities package contains a library for portable access \
to PCI bus configuration space and several utilities based on this library.'
HOMEPAGE = "http://atrey.karlin.mff.cuni.cz/~mj/pciutils.shtml"

PR = "r0"
DEPENDS = "zlib kmod"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

inherit debian-package
PV = "3.2.1"

SRC_URI += "\
        file://configure_debian.patch \
        file://guess-fix_debian.patch \
        file://makefile_debian.patch \
"

PARALLEL_MAKE = ""

PCI_CONF_FLAG = "ZLIB=yes DNS=yes SHARED=yes"

# see configure.patch
do_configure () {
	(
	  cd lib && \
	  ${PCI_CONF_FLAG} ./configure ${PV} ${datadir} ${TARGET_OS} ${TARGET_ARCH}
	)
}

do_compile_prepend () {
	# Avoid this error:  ln: failed to create symbolic link `libpci.so': File exists
	rm -f ${S}/lib/libpci.so
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
	
	# Change directory of binaries according to
	# list of file in Debian package
	mv ${D}${sbindir}/lspci ${D}/${bindir}/
	mv ${D}${sbindir}/pcimodules ${D}/${bindir}/
	mv ${D}${sbindir}/setpci ${D}/${bindir}/
}

PACKAGES =+ "${PN}-ids libpci libpci-dev libpci-dbg"
FILES_${PN}-ids = "${datadir}/pci.ids*"
FILES_libpci = "${libdir}/libpci.so.*"
FILES_libpci-dbg = "${libdir}/.debug"
FILES_libpci-dev = "${libdir}/libpci.a ${libdir}/libpci.la ${libdir}/libpci.so \
                    ${includedir}/pci ${libdir}/pkgconfig"

# Correct .deb file name
DEBIANNAME_libpci = "libpci3"

SUMMARY_${PN}-ids = "PCI utilities - device ID database"
DESCRIPTION_${PN}-ids = "Package providing the PCI device ID database for pciutils."
RDEPENDS_${PN} += "${PN}-ids"
