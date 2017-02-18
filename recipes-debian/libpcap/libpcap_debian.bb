#
# base recipe: meta/recipes-connectivity/libpcap/libpcap_1.6.2.bb
# base branch: master
# base commit: e344dc152d3833fdf8ff6efd028b1b462d417d73
#

SUMMARY = "Interface for user-level network packet capture"
DESCRIPTION = "Libpcap provides a portable framework for low-level network \
monitoring.  Libpcap can provide network statistics collection, \
security monitoring and network debugging."
HOMEPAGE = "http://www.tcpdump.org/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=53067&atid=469577"

LICENSE = "BSD"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=1d4b0366557951c84a94fabe3529f867 \
    file://pcap.h;beginline=1;endline=32;md5=39af3510e011f34b8872f120b1dc31d2 \
"
DEPENDS = "flex-native bison-native"

PR = "r0"
inherit debian-package
PV = "1.6.2"

inherit autotools-brokensep bluetooth

EXTRA_OECONF = "--with-pcap=linux"

PACKAGECONFIG ??= "${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', '${BLUEZ}', '', d)}"
PACKAGECONFIG[bluez4] = "--enable-bluetooth,--disable-bluetooth,bluez4"
# Add a dummy PACKAGECONFIG for bluez5 since it is not supported by libpcap.
PACKAGECONFIG[bluez5] = ",,"
PACKAGECONFIG[canusb] = "--enable-canusb,--enable-canusb=no,libusb"
PACKAGECONFIG[dbus] = "--enable-dbus,--disable-dbus,dbus"
PACKAGECONFIG[libnl] = "--with-libnl,--without-libnl,libnl"

do_configure_prepend () {
	#remove hardcoded references to /usr/include
	sed 's|\([ "^'\''I]\+\)/usr/include/|\1${STAGING_INCDIR}/|g' -i ${S}/configure.in

	if [ ! -e ${S}/acinclude.m4 ]; then
		cat ${S}/aclocal.m4 > ${S}/acinclude.m4
	fi
	sed -i -e's,^V_RPATH_OPT=.*$,V_RPATH_OPT=,' ${S}/pcap-config.in
}

do_install_prepend() {
	if [ -L ${B}/libpcap.so ]; then
		touch ${B}/libpcap.so
	fi
}

FILES_${PN}-dev += "${bindir}/pcap-config"

DEBIANNAME_${PN} = "${PN}0.8"
DEBIANNAME_${PN}-dev = "${PN}0.8-dev"
DEBIANNAME_${PN}-dbg = "${PN}0.8-dbg"

BBCLASSEXTEND="native"
