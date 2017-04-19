SUMMARY = "utilities to read temperature/voltage/fan sensors"
DESCRIPTION = "\
	Lm-sensors is a hardware health monitoring package for Linux. It allows you \
	to access information from temperature, voltage, and fan speed sensors. It \
	works with most newer systems. \
	This package contains programs to help you set up and read data from \
	lm-sensors. \
"
HOMEPAGE = "http://www.lm-sensors.org"

PR = "r2"
inherit debian-package
PV = "3.3.5"

LICENSE = "GPLv2+ & LGPLv2.1+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
	file://COPYING.LGPL;md5=4fbd65380cdd255951079008b364516c"

inherit autotools-brokensep systemd
DEPENDS += "bison bison-native flex-native rrdtool"

EXTRA_OEMAKE = 'EXLDFLAGS="${LDFLAGS}" \
	MACHINE=${TARGET_ARCH} PREFIX=${prefix} MANDIR=${mandir} \
	LIBDIR=${libdir} PROG_EXTRA=sensord\
	CC="${CC}" AR="${AR}"'

PACKAGES =+ "fancontrol libsensors sensord"
SYSTEMD_SERVICE_fancontrol = "fancontrol.service"

#install follow Debian jessie
do_install_append() {
	install -D -m 0644 ${S}/debian/sensord.default \
		${D}${sysconfdir}/default/sensord
	install -D -m 0755 ${S}/debian/fancontrol.init \
		${D}${sysconfdir}/init.d/fancontrol
	install -m 0755 ${S}/debian/lm-sensors.init \
		${D}${sysconfdir}/init.d/lm-sensors
	install -m 0755 ${S}/debian/sensord.init \
		${D}${sysconfdir}/init.d/sensord
	install -D -m 0644 ${S}/debian/sensord.logcheck.ignore.workstation \
		${D}${sysconfdir}/logcheck/ignore.d.workstation/sensord

	install -D -m 0644 ${S}/debian/fancontrol.service \
		${D}${systemd_system_unitdir}/fancontrol.service
	install -m 0644 ${S}/debian/lm-sensors.service \
		${D}${systemd_system_unitdir}/lm-sensors.service
}
DEBIANNAME_${PN}-dev = "libsensors4-dev"

FILES_fancontrol = "\
	${sysconfdir}/init.d/fancontrol ${sbindir}/fancontrol \
	${systemd_system_unitdir}/fancontrol.service \
	${sbindir}/pwmconfig \
	"
FILES_libsensors = "\
	${sysconfdir}/sensors.d ${sysconfdir}/sensors3.conf \
	${libdir}/libsensors.so.* \
	"
FILES_sensord = "\
	${sysconfdir}/default/sensord ${sysconfdir}/init.d/sensord \
	${sysconfdir}/logcheck ${sbindir}/sensord \
	"
FILES_${PN} += "${systemd_system_unitdir}/lm-sensors.service"

#follow debian/control
RDEPENDS_fancontrol += "lsb-base"
RDEPENDS_sensord += "${PN} lsb-base"
RDEPENDS_${PN}-dev += "libsensors"
