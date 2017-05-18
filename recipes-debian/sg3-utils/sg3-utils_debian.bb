SUMMARY = "utilities for devices using the SCSI command set"
DESCRIPTION = "Most OSes have SCSI pass-through interfaces that enable user space programs\n\
to send SCSI commands to a device and fetch the response. With SCSI to ATA\n\
Translation (SAT) many ATA disks now can process SCSI commands. Typically\n\
each utility in this package implements one SCSI command. See the draft\n\
standards at www.t10.org for SCSI command definitions plus SAT. ATA\n\
commands are defined in the draft standards at www.t13.org . For a mapping\n\
between supported SCSI and ATA commands and utility names in this package\n\
see the COVERAGE file."
HOMEPAGE = "http://sg.danny.cz/sg/"
LICENSE = "BSD & GPL-2+"
SECTION = "admin"
DEPENDS = "libtool"
LIC_FILES_CHKSUM = "file://COPYING;md5=f90da7fc52172599dbf082d7620f18ca"

PR = "r0"
inherit debian-package
PV = "1.39"

inherit autotools-brokensep

PACKAGES =+ "libsgutils libsgutils-dev libsgutils-staticdev"

FILES_libsgutils = "${libdir}/libsgutils2.so.2 \
${libdir}/libsgutils2.so.2.0.0"

FILES_libsgutils-dev = "${includedir} \
${libdir}/libsgutils2.la \
${libdir}/libsgutils2.so"

FILES_libsgutils-staticdev = "${libdir}/libsgutils2.a"

RDEPENDS_${PN} += "libsgutils"
