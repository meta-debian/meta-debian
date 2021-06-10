# base-recipe: meta/recipes-bsp/setserial/setserial_2.17.bb
# base-branch: morty

SUMMARY = "controls configuration of serial ports"
DESCRIPTION = "Set and/or report the configuration information associated with\n\
 a serial port. This information includes what I/O port and which IRQ\n\
 a particular serial port is using.\n\
 .\n\
 This version has a completely new approach to configuration, so if you\n\
 have a setup other than the standard ttyS0 and 1, you will have to get\n\
 your hands dirty.\n\
 .\n\
 By default, only COM1-4 are configured by the kernel, using IRQ 3 and 4.\n\
 If you have other serial ports (such as an AST Fourport card), or\n\
 if you have mapped the IRQs differently (perhaps COM3 and 4 to other\n\
 IRQs to allow concurrent access with COM1 and 2) then you must have this\n\
 package."
HOMEPAGE = "http://ftp.mcc.ac.uk/pub/linux/"

inherit debian-package
PV = "2.17"

LICENSE = "GPLv2.0"
LIC_FILES_CHKSUM = "file://version.h;endline=6;md5=8b700d995896017d3d87b789fd288bd5"

inherit autotools-brokensep

EXTRA_OEMAKE += "CXXFLAGS="${CXXFLAGS}""

do_compile_prepend() {
	echo "#undef __mc68000__" > ${S}/gorhack.h
}

do_install() {
	install -d ${D}${base_bindir} \
	           ${D}${docdir}/setserial \
	           ${D}${mandir}/man8 \
	           ${D}${sysconfdir}/init.d \
	           ${D}${sysconfdir}/modutils \
	           ${D}${localstatedir}/lib/setserial

	install -g root -m 755 -o root ${S}/setserial ${D}${base_bindir}
	install -g root -m 644 -o root ${S}/setserial.8 \
		${D}${mandir}/man8/setserial.8
	install -g root -m 755 -o root ${S}/rc.serial \
		${D}${sysconfdir}/init.d/setserial
	install -g root -m 755 -o root ${S}/rc.etc-setserial \
		${D}${sysconfdir}/init.d/etc-setserial
	install -g root -m 644 -o root ${S}/serial.conf \
		${D}${docdir}/setserial/serial.conf
}
