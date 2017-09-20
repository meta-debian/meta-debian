SUMMARY = "report kernel-detected PCI and ECC RAM errors"
DESCRIPTION = "This package contains the user-space utilities for use with the EDAC\n\
kernel subsystem.  EDAC (Error Detection and Correction) is a set of\n\
Linux kernel modules for handling hardware-related errors.  Currently\n\
its major focus is ECC memory error handling. However it also detects\n\
and reports PCI bus parity errors.\n\
.\n\
PCI parity errors are supported on all architectures (and are a\n\
mandatory part of the PCI specification).\n\
.\n\
Main memory ECC drivers are memory controller specific.  At the time\n\
of writing, drivers exist for many x86-specific chipsets and CPUs,\n\
and some PowerPC, and MIPS systems."
HOMEPAGE = "http://sourceforge.net/projects/edac-utils"

inherit debian-package
PV = "0.18"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

inherit autotools

EXTRA_OECONF += "disable-rpath"

DEPENDS += "sysfsutils chrpath-native"

do_install_append() {
	# Follow debian/rules
	chrpath -d ${D}${bindir}/edac-util

	install -D -m 0644 ${S}/debian/edac-utils.edac.default \
		${D}${sysconfdir}/default/edac
}

PACKAGES =+ "libedac libedac-dev"

FILES_libedac = "${libdir}/libedac${SOLIBS}"
FILES_libedac-dev = "${libdir}/libedac.so \
                     ${includedir}/edac.h"

RDEPENDS_${PN} += "lsb-base"
RPROVIDES_libedac += "libedac1"
