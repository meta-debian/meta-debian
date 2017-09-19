SUMMARY = "Avahi IPv4LL network address configuration daemon"
DESCRIPTION = 'Avahi is a fully LGPL framework for Multicast DNS Service Discovery. It \
allows programs to publish and discover services and hosts running on a local network \
with no specific configuration. This tool implements IPv4LL, "Dynamic Configuration of \
IPv4 Link-Local Addresses" (IETF RFC3927), a protocol for automatic IP address \
configuration from the link-local 169.254.0.0/16 range without the need for a central \
server.'
HOMEPAGE = "http://avahi.org/"

inherit debian-package
PV = "0.6.31"

LICENSE = "LGPLv2.1+ & BSD-2-Clause & BSD-3-Clause"
LIC_FILES_CHKSUM = "\
	file://LICENSE;md5=2d5025d4aa3495befef8f17206a5b0a1 \
	file://avahi-compat-howl/include/salt/socket.h;beginline=4;endline=30;md5=325029639b1715f7512b7263dd0204e1 \
	file://avahi-compat-libdns_sd/dns_sd.h;endline=27;md5=81352476b6730ced9a1f3c21ca22e209"

inherit autotools pkgconfig useradd pythonnative

EXTRA_OECONF += "-with-distro=debian \
                 --disable-qt3 \
                 --disable-qt4 \
                 --disable-mono \
                 --enable-compat-libdns_sd \
                 --disable-monodoc \
                 --disable-doxygen-doc \
                 --disable-manpages \
                 ${@bb.utils.contains('DISTRO_FEATURES','systemd','--with-systemdsystemunitdir=${systemd_system_unitdir}','--without-systemdsystemunitdir',d)} \
                 "

CACHED_CONFIGUREVARS += "py_cv_mod_gdbm_=yes"
# need to export these variables for python runtime
# fix error:
#       PREFIX = os.path.normpath(sys.prefix).replace( os.getenv("BUILD_SYS"), os.getenv("HOST_SYS") )
#       TypeError: Can't convert 'NoneType' object to str implicitly
export BUILD_SYS
export HOST_SYS

DEPENDS += "intltool-native glib-2.0-native libdaemon gdbm expat libcap python-dbus-native"

PACKAGECONFIG ??= "dbus gtk python"
PACKAGECONFIG[dbus] = "--enable-dbus,--disable-dbus,dbus"
PACKAGECONFIG[gtk] = "--enable-gtk,--disable-gtk,gtk+"
PACKAGECONFIG[gtk3] = "--enable-gtk3,--disable-gtk3,gtk+3"
PACKAGECONFIG[pygtk] = "--enable-pygtk,--disable-pygtk,pygtk"
PACKAGECONFIG[python] = "--enable-python,--disable-python,python python-dbus"

do_install_append() {
	install -d ${D}${sysconfdir}/default \
	           ${D}${sysconfdir}/network/if-down.d \
	           ${D}${sysconfdir}/network/if-post-down.d \
	           ${D}${sysconfdir}/network/if-up.d

	install -m 0644 ${S}/debian/avahi-daemon.default \
		${D}${sysconfdir}/default/avahi-daemon
	install -m 0755 ${S}/debian/avahi-autoipd.if-up \
		${D}${sysconfdir}/network/if-up.d/avahi-autoipd
	install -m 0755 ${S}/debian/avahi-daemon.if-up \
		${D}${sysconfdir}/network/if-up.d/avahi-daemon
	install -m 0755 ${S}/debian/avahi-autoipd.if-down \
		${D}${sysconfdir}/network/if-down.d/avahi-autoipd
	ln -sf ../if-up.d/avahi-daemon \
		${D}${sysconfdir}/network/if-post-down.d/avahi-daemon

	install -D -m 0755 ${S}/debian/avahi-daemon.resolvconf \
		${D}${sysconfdir}/resolvconf/update-libc.d/avahi-daemon
	install -D -m 0755 ${S}/debian/avahi-daemon-check-dns.sh \
		${D}${libdir}/avahi/avahi-daemon-check-dns.sh

	# Follow debian/rules
	mv ${D}${sysconfdir}/dhcp/dhclient-exit-hooks.d/avahi-autoipd \
		${D}${sysconfdir}/dhcp/dhclient-exit-hooks.d/zzz_avahi-autoipd

	# Follow debian/libavahi-compat-libdnssd-dev.links
	ln -sf avahi-compat-libdns_sd/dns_sd.h ${D}${includedir}/dns_sd.h

	# Remove unwanted files
	rm -rf ${D}${localstatedir}/run \
	       ${D}${sysconfdir}/avahi/services \
	       ${D}${bindir}/avahi-discover-standalone \
	       ${D}${bindir}/avahi-bookmarks \
	       ${D}${libdir}/*.la
	find ${D}${libdir} -type f -name "*.pyo" -exec rm -f {} \;
}
# Base on debian/avahi-autoipd.postinst
USERADD_PACKAGES = "${PN}-autoipd ${PN}-daemon"
USERADD_PARAM_${PN}-autoipd = "--system --home /var/lib/avahi-autoipd \
                       --no-create-home avahi-autoipd"

# Base on debian/avahi-daemon.postinst
USERADD_PARAM_${PN}-daemon = "--system --home /var/run/avahi-daemon \
                               --no-create-home avahi"
GROUPADD_PARAM_${PN}-daemon = "-r netdev"

PACKAGES =+ "${PN}-autoipd ${PN}-daemon ${PN}-dnsconfd ${PN}-ui-utils \
             ${PN}-utils libavahi-client-dev libavahi-client libavahi-common-data \
             libavahi-common-dev libavahi-common libavahi-compat-libdnssd-dev \
             libavahi-compat-libdnssd libavahi-core-dev libavahi-core libavahi-glib-dev \
             libavahi-glib libavahi-gobject-dev libavahi-gobject libavahi-ui-dev \
             libavahi-ui python-${PN}"

FILES_${PN}-autoipd = "${sysconfdir}/avahi/avahi-autoipd.action \
                       ${sysconfdir}/dhcp/* \
                       ${sysconfdir}/network/if-down.d/avahi-autoipd \
                       ${sysconfdir}/network/if-up.d/avahi-autoipd \
                       ${sbindir}/avahi-autoipd"
FILES_${PN}-daemon = "${sysconfdir}/avahi/avahi-daemon.conf \
                      ${sysconfdir}/avahi/hosts \
                      ${sysconfdir}/dbus-1/system.d/avahi-dbus.conf \
                      ${sysconfdir}/default/avahi-daemon \
                      ${sysconfdir}/init.d/avahi-daemon \
                      ${sysconfdir}/network/if-post-down.d/avahi-daemon \
                      ${sysconfdir}/network/if-up.d/avahi-daemon \
                      ${sysconfdir}/resolvconf/update-libc.d/avahi-daemon \
                      ${systemd_system_unitdir}/avahi-daemon.* \
                      ${libdir}/avahi/avahi-daemon-check-dns.sh \
                      ${sbindir}/avahi-daemon \
                      ${datadir}/avahi/avahi-service.dtd \
                      ${datadir}/dbus-1/* \
                      "
FILES_${PN}-dnsconfd = "${sysconfdir}/avahi/avahi-dnsconfd.action \
                        ${sysconfdir}/init.d/avahi-dnsconfd \
                        ${systemd_system_unitdir}/avahi-dnsconfd.service \
                        ${sbindir}/avahi-dnsconfd"
FILES_${PN}-ui-utils = "${bindir}/bshell \
                        ${bindir}/bssh \
                        ${bindir}/bvnc \
                        ${datadir}/applications/*"
FILES_${PN}-utils = "${bindir}/avahi-*"
FILES_libavahi-client-dev = "${includedir}/avahi-client/*.h \
                             ${libdir}/libavahi-client.so \
                             ${libdir}/pkgconfig/avahi-client.pc"
FILES_libavahi-client = "${libdir}/libavahi-client${SOLIBS}"
FILES_libavahi-common-data = "${libdir}/avahi/service-types.db \
                              ${datadir}/avahi/service-types"
FILES_libavahi-common-dev = "${includedir}/avahi-common/*.h \
                             ${libdir}/libavahi-common.so"
FILES_libavahi-common = "${libdir}/libavahi-common${SOLIBS}"
FILES_libavahi-compat-libdnssd-dev = "${includedir}/avahi-compat-libdns_sd/dns_sd.h \
                                      ${includedir}/dns_sd.h \
                                      ${libdir}/libdns_sd.so \
                                      ${libdir}/pkgconfig/avahi-compat-libdns_sd.pc"
FILES_libavahi-compat-libdnssd = "${libdir}/libdns_sd${SOLIBS}"
FILES_libavahi-core-dev = "${includedir}/avahi-core/*.h \
                           ${libdir}/libavahi-core.so \
                           ${libdir}/pkgconfig/avahi-core.pc"
FILES_libavahi-core = "${libdir}/libavahi-core${SOLIBS}"
FILES_libavahi-glib-dev = "${includedir}/avahi-glib/*.h \
                           ${libdir}/libavahi-glib.so \
                           ${libdir}/pkgconfig/avahi-glib.pc"
FILES_libavahi-glib = "${libdir}/libavahi-glib${SOLIBS}"
FILES_libavahi-gobject-dev = "${includedir}/avahi-gobject/*.h \
                              ${libdir}/libavahi-gobject.so \
                              ${libdir}/pkgconfig/avahi-gobject.pc"
FILES_libavahi-gobject = "${libdir}/libavahi-gobject${SOLIBS}"
FILES_libavahi-ui-dev = "${includedir}/avahi-ui/*.h \
                         ${libdir}/libavahi-ui.so \
                         ${libdir}/pkgconfig/avahi-ui.pc"
FILES_libavahi-ui = "${libdir}/libavahi-ui${SOLIBS}"
FILES_python-${PN} = "${libdir}/python*"

DEBIANNAME_libavahi-compat-libdnssd = "libavahi-compat-libdnssd1"
DEBIAN_NOAUTONAME_libavahi-compat-libdnssd-dev = "1"
RPROVIDES_libavahi-client += "libavahi-client3"
RPROVIDES_libavahi-common += "libavahi-common3"
RPROVIDES_libavahi-compat-libdnssd += "libavahi-compat-libdnssd1"
RPROVIDES_libavahi-core += "libavahi-core7"
RPROVIDES_libavahi-glib += "libavahi-glib1"
RPROVIDES_libavahi-gobject += "libavahi-gobject0"
RPROVIDES_libavahi-ui += "libavahi-ui0"

#Base on debian/control
RDEPENDS_${PN}-daemon += "dbus lsb-base bind9-host init-system-helpers \
                          libavahi-common libavahi-core libcap libdaemon \
                          dbus-lib libexpat"
RSUGGESTS_${PN}-daemon += "${PN}-autoipd"
RDEPENDS_${PN}-dnsconfd += "${PN}-daemon lsb-base init-system-helpers libavahi-common \
                            libdaemon"
RRECOMMENDS_${PN}-dnsconfd += "resolvconf"
RDEPENDS_${PN}-autoipd += "libdaemon"
RRECOMMENDS_${PN}-autoipd += "isc-dhcp-client iproute2"
RDEPENDS_${PN}-utils += "avahi-daemon libavahi-client libavahi-common gdbm"
RDEPENDS_libavahi-common += "libavahi-common-data"
RDEPENDS_libavahi-core += "libavahi-common"
RDEPENDS_libavahi-core-dev += "libavahi-common-dev"
RDEPENDS_libavahi-client += "libavahi-common dbus-lib"
RDEPENDS_libavahi-client-dev += "libavahi-common-dev dbus-dev"
RDEPENDS_libavahi-glib += "libavahi-common"
RDEPENDS_libavahi-glib-dev += "glib-2.0-dev libavahi-common-dev"
RDEPENDS_libavahi-gobject += "libavahi-common libavahi-client libavahi-glib glib-2.0"
RDEPENDS_libavahi-gobject-dev += "glib-2.0-dev libavahi-client-dev libavahi-glib-dev"
RDEPENDS_libavahi-compat-libdnssd += "libavahi-common libavahi-client"
RDEPENDS_libavahi-compat-libdnssd-dev += "libavahi-client-dev"
RDEPENDS_libavahi-ui += "libavahi-common libavahi-client libavahi-glib gdbm \
                         glib-2.0 gtk+"
RDEPENDS_libavahi-ui-dev += "libavahi-client-dev libavahi-glib-dev gtk+-dev"
RDEPENDS_${PN}-ui-utils += "libavahi-common libavahi-client"
RDEPENDS_python-${PN} += "python-gdbm python-dbus libavahi-common-data"

# disable install parallel
PARALLEL_MAKEINST = ""
