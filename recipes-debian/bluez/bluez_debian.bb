#
# base recipe: meta/recipes-connectivity/bluez5/bluez5_5.15.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "5.43"

SUMMARY = "Linux Bluetooth Stack Userland V5"
DESCRIPTION = "Linux Bluetooth stack V5 userland components.  These include a system configurations, daemons, tools and system libraries."
HOMEPAGE = "http://www.bluez.org"
LICENSE = "GPLv2+ & LGPLv2.1+ & MIT & GFDL-1.1+"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=12f884d2ae1ff87c09e5b7ccc2c4ca7e \
	file://COPYING.LIB;md5=fb504b67c50331fc78734fed90fb0e09 \
	file://src/main.c;beginline=1;endline=24;md5=9bc54b93cd7e17bf03f52513f39f926e \
	file://tools/ubcsp.h;endline=27;md5=bd2dee23c6c551d84e308d07c2953479 \
"
DEPENDS = "udev libusb dbus-glib glib-2.0 check readline"

# init.d/bluetooth require lsb-base
RDEPENDS_${PN} += "lsb-base"

RCONFLICTS_${PN} = "bluez4"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'alsa', 'alsa', '', d)} obex-profiles"
PACKAGECONFIG[obex-profiles] = "--enable-obex,--disable-obex,libical"

SRC_URI += "\
	file://bluetooth.conf \
"

inherit autotools-brokensep pkgconfig systemd

PARALLEL_MAKE = ""

# Follow debian/rules
EXTRA_OECONF = " \
	--libexecdir=${nonarch_libdir} \
	--enable-static \
	--enable-tools \
	--enable-cups \
	--enable-datafiles \
	--enable-debug \
	--enable-library \
	--enable-monitor \
	--enable-udev \
	--enable-client \
	${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '--with-systemdsystemunitdir=${systemd_unitdir}/system/', '--disable-systemd', d)} \
	--enable-threads \
	--enable-sixaxis \
	--enable-experimental \
"

do_install_append() {
	# In Debian, hciconfig is installed to /bin
	if [ "${bindir}" != "${base_bindir}" ]; then
		install -d ${D}${base_bindir}
		mv ${D}${bindir}/hciconfig ${D}${base_bindir}/
	fi

	install -d ${D}${sysconfdir}/bluetooth/
	if [ -f ${S}/profiles/audio/audio.conf ]; then
		install -m 0644 ${S}/profiles/audio/audio.conf ${D}/${sysconfdir}/bluetooth/
	fi
	if [ -f ${S}/profiles/network/network.conf ]; then
		install -m 0644 ${S}/profiles/network/network.conf ${D}/${sysconfdir}/bluetooth/
	fi
	if [ -f ${S}/profiles/input/input.conf ]; then
		install -m 0644 ${S}/profiles/input/input.conf ${D}/${sysconfdir}/bluetooth/
	fi
	# at_console doesn't really work with the current state of OE, so punch some more holes so people can actually use BT
	install -m 0644 ${WORKDIR}/bluetooth.conf ${D}/${sysconfdir}/dbus-1/system.d/

	# Install configuration files, scripts from ${S}/debian/
	install -d ${D}${sysconfdir}/default
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${sbindir}
	install -m 0644 ${S}/debian/bluez.bluetooth.default ${D}${sysconfdir}/default/bluetooth
	install -m 0755 ${S}/debian/bluez.bluetooth.init ${D}${sysconfdir}/init.d/bluetooth

	ln -s ../lib/bluetooth/bluetoothd ${D}${sbindir}/bluetoothd

	# Base on debian/bluez-cups.install
	mv ${D}${libdir}/cups ${D}${nonarch_libdir}/
}

ALLOW_EMPTY_libasound-module-bluez = "1"
PACKAGES =+ " \
	libasound-module-bluez \
	${DPN}-obex \
	${DPN}-cups \
	${DPN}-hcidump \
	libbluetooth \
"

FILES_${DPN}-cups = "${nonarch_libdir}/cups/backend/bluetooth"
FILES_${DPN}-hcidump = "${bindir}/hcidump"
FILES_libbluetooth = "${libdir}/libbluetooth.so.* ${libdir}/bluetooth/plugins/*.so"

FILES_libasound-module-bluez = "${libdir}/alsa-lib/lib*.so ${datadir}/alsa"
FILES_${PN} += " \
	${base_bindir}/hciconfig \
	${sbindir}/bluetoothd \
	${libdir}/bluetooth/plugins/*.so \
	${nonarch_base_libdir}/udev/ \
	${systemd_unitdir}/ \
	${datadir}/dbus-1 \
	${nonarch_libdir}/bluetooth/bluetoothd \
"
FILES_${PN}-dev += "\
	${libdir}/bluetooth/plugins/*.la \
	${libdir}/alsa-lib/*.la \
"

FILES_${PN}-staticdev += "${libdir}/bluetooth/plugins/*.a"

FILES_${DPN}-obex = " \
	${nonarch_libdir}/bluetooth/obexd \
	${nonarch_libdir}/systemd/user/obex.service \
	${datadir}/dbus-1/services/org.bluez.obex.service \
"
SYSTEMD_SERVICE_${DPN}-obex = "obex.service"

FILES_${PN}-dbg += "\
	${libdir}/bluetooth/.debug \
	${libdir}/bluetooth/plugins/.debug \
	${libdir}/cups/backend/.debug \
	${libdir}/*/.debug \
	*/udev/.debug \
"

SYSTEMD_SERVICE_${PN} = "bluetooth.service"

EXCLUDE_FROM_WORLD = "1"

DEBIANNAME_libbluetooth = "libbluetooth3"
DEBIANNAME_${DPN}-obex = "${DPN}-obexd"
DEBIANNAME_${DPN}-dev = "libbluetooth-dev"
