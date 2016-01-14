#
# base recipe: /meta/recipes-connectivity/bind/bind_9.10.2-P4.bb
# base branch: master
#

PR = "r0"

inherit debian-package

LICENSE = "ISC & BSD"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=a3df5f651469919a0e6cb42f84fb6ff1"

DEPENDS = "openssl openssl-native libcap"

DEBIAN_PATCH_TYPE = "nopatch"

ENABLE_IPV6 = "--enable-ipv6=${@bb.utils.contains('DISTRO_FEATURES', 'ipv6', 'yes', 'no', d)}"
EXTRA_OECONF = " ${ENABLE_IPV6} --with-randomdev=/dev/random --disable-threads \
		 --disable-devpoll --disable-epoll --with-gost=no \
		 --with-gssapi=no --with-ecdsa=yes \
		 --sysconfdir=${sysconfdir}/bind \
		 --without-openssl --disable-kqueue --with-gssapi=no \
	       "
inherit autotools update-rc.d systemd useradd pkgconfig

PACKAGECONFIG ?= ""
PACKAGECONFIG[httpstats] = "--with-libxml2,--without-libxml2,libxml2"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system --home /var/cache/bind --no-create-home \
		       --user-group bind"

INITSCRIPT_NAME = "bind"
INITSCRIPT_PARAMS = "defaults"

PARALLEL_MAKE = ""

RDEPENDS_${PN} = "python-core"
RDEPENDS_${PN}-dev = ""

PACKAGES =+ "${PN}-host ${PN}utils dnsutils host libbind-dev \
	libbind-export-dev libbind9-90 libdns-export100 libdns100 \
	libirs-export91 libisc-export95 libisc95 libisccc90 \
	libisccfg-export90 libisccfg90 liblwres90 lwresd"

do_compile_prepend() {
	export LD_LIBRARY_PATH=${STAGING_LIBDIR}:$LD_LIBRARY_PATH
}

do_install_append() {
	install -D -m 644 ${S}/debian/apparmor-profile ${D}${sysconfdir}/apparmor.d/usr.sbin.named
	install -D -m 644 ${S}/debian/apparmor-profile.local ${D}${sysconfdir}/apparmor.d/local/usr.sbin.named

	install -D -m 444 ${S}/debian/db.0 ${D}${sysconfdir}/bind/db.0
	install -D -m 444 ${S}/debian/db.127 ${D}${sysconfdir}/bind
	install -D -m 444 ${S}/debian/db.0 ${D}${sysconfdir}/bind/db.255
	install -D -m 444 ${S}/debian/db.empty ${D}${sysconfdir}/bind
	install -D -m 444 ${S}/debian/db.local ${D}${sysconfdir}/bind
	install -D -m 444 ${S}/debian/db.root ${D}${sysconfdir}/bind
	install -D -m 444 ${S}/debian/named.conf ${D}${sysconfdir}/bind
	install -D -m 444 ${S}/debian/named.conf.default-zones ${D}${sysconfdir}/bind
	install -D -m 444 ${S}/debian/named.conf.local ${D}${sysconfdir}/bind
	install -D -m 444 ${S}/debian/zones.rfc1918 ${D}${sysconfdir}/bind

	install -D ${S}/debian/ip-up.d ${D}${sysconfdir}/network/if-up.d/bind9
	install -D ${S}/debian/ip-down.d ${D}${sysconfdir}/network/if-down.d/bind9

	install -D ${S}/debian/ip-up.d ${D}${sysconfdir}/ppp/if-up.d/bind9
	install -D ${S}/debian/ip-down.d ${D}${sysconfdir}/ppp/if-down.d/bind9

	install -D -m 644 ${S}/debian/bind9.ufw.profile ${D}${sysconfdir}/ufw/applications.d/bind9

	install -D -m 644 ${S}/bind9.service ${D}${systemd_system_unitdir}/bind9.service
	install -D -m 644 ${S}/bind9-resolvconf.service ${D}${systemd_system_unitdir}/bind9-resolvconf.service
	install -D -m 644 ${S}/debian/bind9.tmpfile ${D}${libdir}/tmpfiles.d/lwresd.conf
	install -D ${S}/debian/lwresd.service ${D}${systemd_system_unitdir}/lwresd.service

	ln -sf libbind9.so.90.0.9 ${D}${libdir}/libbind9.so
	ln -sf libdns.so.100.2.2 ${D}${libdir}/libdns.so
	ln -sf libisc.so.95.5.0 ${D}${libdir}/libisc.so
	ln -sf libisccc.so.90.0.6 ${D}${libdir}/libisccc.so
	ln -sf libisccfg.so.90.1.0 ${D}${libdir}/libisccfg.so
	ln -sf liblwres.so.90.0.7 ${D}${libdir}/liblwres.so

	ln -sf libdns-export.so.100.2.2 ${D}${libdir}/libdns-export.so
	ln -sf libirs-export.so.91.0.0 ${D}${libdir}/libirs-export.so
	ln -sf libisc-export.so.95.5.0 ${D}${libdir}/libisc-export.so
	ln -sf libisccfg-export.so.90.1.0 ${D}${libdir}/libisccfg-export.so

	ln -sf libbind9.so.90.0.9 ${D}${libdir}/libbind9.so.90

	ln -sf libdns-export.so.100.2.2 ${D}${libdir}/libdns-export.so.100

	ln -sf libdns.so.100.2.2 ${D}${libdir}/libdns.so.100

	ln -sf libirs-export.so.91.0.0 ${D}${libdir}/libirs-export.so.91

	ln -sf libisc-export.so.95.5.0 ${D}${libdir}/libisc-export.so.95

	ln -sf libisc.so.95.5.0 ${D}${libdir}/libisc.so.95

	ln -sf libisccc.so.90.0.6 ${D}${libdir}/libisccc.so.90

	ln -sf libisccfg-export.so.90.1.0 ${D}${libdir}/libisccfg-export.so.90

	ln -sf libisccfg.so.90 ${D}${libdir}/libisccfg.so.90.1.0

	ln -sf liblwres.so.90 ${D}${libdir}/liblwres.so.90.0.7

}

CONFFILES_${PN} = " \
	${sysconfdir}/bind/named.conf \
	${sysconfdir}/bind/named.conf.local \
	${sysconfdir}/bind/named.conf.options \
	${sysconfdir}/bind/db.0 \
	${sysconfdir}/bind/db.127 \
	${sysconfdir}/bind/db.empty \
	${sysconfdir}/bind/db.local \
	${sysconfdir}/bind/db.root \
	"
SYSTEMD_SERVICE_${PN} = "bind9.service bind9-resolvconf.service"
SYSTEMD_SERVICE_lwresd = "lwresd.service"

FILES_${PN}-host = " \
	${bindir}/host \
	"

FILES_${PN}utils = " \
	${sbindir}/dnssec-checkds \
	${sbindir}/dnssec-coverage \
	${sbindir}/dnssec-dsfromkey \
	${sbindir}/dnssec-keyfromlabel \
	${sbindir}/dnssec-keygen \
	${sbindir}/dnssec-revoke \
	${sbindir}/dnssec-settime \
	${sbindir}/dnssec-signzone \
	${sbindir}/dnssec-verify \
	${sbindir}/named-checkconf \
	${sbindir}/named-checkzone \
	${sbindir}/named-compilezone \
	${sbindir}/rndc \
	${sbindir}/rndc-confgen \
	"
FILES_dnsutils = " \
	${bindir}/dig \
	${bindir}/nslookup \
	${bindir}/nsupdate \
	"

FILES_libbind-dev = " \
	${bindir}/isc-config.sh \
	${libdir}/libbind9.so \
	${libdir}/libdns.so \
	${libdir}/libisc.so \
	${libdir}/libisccc.so \
	${libdir}/libisccfg.so \
	${libdir}/liblwres.so \
	"

FILES_libbind-export-dev = " \
	${libdir}/libdns-export.so \
	${libdir}/libirs-export.so \
	${libdir}/libisc-export.so \
	${libdir}/libisccfg-export.so \
	"
FILES_libbind9-90 = " \
	${libdir}/libbind9.so.90 \
	${libdir}/libbind9.so.90.0.9 \
	"

FILES_libdns-export100 = " \
	${libdir}/libdns-export.so.100 \
	${libdir}/libdns-export.so.100.2.2 \
	"

FILES_libdns100 = " \
	${libdir}/libdns.so.100 \
	${libdir}/libdns.so.100.2.2 \
	"

FILES_libirs-export91 = " \
	${libdir}/libirs-export.so.91 \
	${libdir}/libirs-export.so.91.0.0 \
	"

FILES_libisc-export95 = " \
	${libdir}/libisc-export.so.95 \
	${libdir}/libisc-export.so.95.5.0 \
	"

FILES_libisc95 = " \
	${libdir}/libisc.so.95 \
	${libdir}/libisc.so.95.5.0 \
	"

FILES_libisccc90 = " \
	${libdir}/libisccc.so.90 \
	${libdir}/libisccc.so.90.0.6 \
	"

FILES_libisccfg-export90 = " \
	${libdir}/libisccfg-export.so.90 \
	${libdir}/libisccfg-export.so.90.1.0 \
	"

FILES_libisccfg90 = " \
	${libdir}/libisccfg.so.90 \
	${libdir}/libisccfg.so.90.1.0 \
	"

FILES_liblwres90 = " \
	${libdir}/liblwres.so.90 \
	${libdir}/liblwres.so.90.0.7 \
	"

FILES_lwresd = " \
	${sysconfdir}/init.d/lwresd \
	${systemd_system_unitdir}/lwresd.service \
	${libdir}/tmpfiles.d/lwresd.conf \
	${sbindir}/lwresd \
	"

FILES_${PN} += "${localstatedir} /run"	 
