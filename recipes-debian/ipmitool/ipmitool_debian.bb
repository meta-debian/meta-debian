SUMMARY = "utility for IPMI control"
DESCRIPTION = "ipmitool is a utility for managing and configuring devices \
that support the Intelligent Platform Management Interface"
HOMEPAGE = "http://ipmitool.sourceforge.net"

PR = "r0"
inherit debian-package
PV = "1.8.14"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=9aa91e13d644326bf281924212862184"

inherit autotools

do_install_append() {
	#Create folders
	install -d ${D}${sysconfdir}
	install -d ${D}${sysconfdir}/default
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${base_libdir}
	install -d ${D}${base_libdir}/systemd
	install -d ${D}${base_libdir}/systemd/system
	install -d ${D}${libdir}
	install -d ${D}${libdir}/modules-load.d

	#Install etc/default/ipmievd
	install -m 0644 ${S}/debian/ipmitool.ipmievd.default \
			${D}${sysconfdir}/default/ipmievd
	#Install etc/init.d/ipmievd
	install -m 0755 ${S}/debian/ipmitool.ipmievd.init \
			${D}${sysconfdir}/init.d/ipmievd
	#Install lib/systemd/system/ipmievd.service
	install -m 0644 ${S}/debian/systemd/ipmitool.ipmievd.service \
			${D}${base_libdir}/systemd/system/ipmievd.service
	#Install usr/lib/modules-load.d/ipmievd.conf
	install -m 0644 ${S}/debian/systemd/ipmitool.conf \
			${D}${libdir}/modules-load.d/ipmievd.conf
	
}

FILES_${PN} += "${base_libdir}/* ${libdir}/*"
