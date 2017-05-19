#
# Base recipe: meta-oe/recipes-support/usb-modeswitch/usb-modeswitch_2.2.0.bb
# Base branch: jethro
#

SUMMARY = "A mode switching tool for controlling 'flip flop' (multiple device) USB gear"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

PR = "r0"

inherit debian-package systemd
PV = "2.2.0+repack0"

DEPENDS = "libusb1"

EXTRA_OEMAKE = "TCL=${bindir}/tclsh"

FILES_${PN} = "${bindir} ${sysconfdir} ${nonarch_base_libdir}/udev/usb_modeswitch ${sbindir} ${localstatedir}/lib/usb_modeswitch"
RDEPENDS_${PN} = "tcl"

do_install() {
	oe_runmake DESTDIR=${D} install
	mkdir -p ${D}${sysconfdir}/init
	install -m 0644 ${S}/usb-modeswitch-upstart.conf ${D}${sysconfdir}/init/
	mkdir -p ${D}${systemd_unitdir}/system
	install -m 0644 ${S}/usb_modeswitch@.service ${D}${systemd_unitdir}/system
}

FILES_${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_unitdir}', '', d)}"
