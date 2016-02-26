#
# base recipe: /meta/recipes-connectivity/bind/bind_9.10.2-P4.bb
# base branch: master
#

SUMMARY = "ISC Internet Domain Name Server"
HOMEPAGE = "http://www.isc.org/sw/bind/"
SECTION = "console/network"

PR = "r3"
DPN = "bind9"
inherit debian-package

LICENSE = "ISC & BSD"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=a3df5f651469919a0e6cb42f84fb6ff1"

DEPENDS = "openssl libcap krb5"
DEBIAN_PATCH_TYPE = "nopatch"

# fix-configure-for-gssapi.patch 
#	fix path to gssapi.h file

# not-build-dst.patch
#	 temporary, avoid "wrong ELF class: ELFCLASS64", do not build dst 

SRC_URI += " \
	file://fix-configure-for-gssapi.patch \
	file://not-build-dst.patch \
"

EXTRA_OECONF = " \
	--sysconfdir=/etc/bind \
	--enable-threads \
	--enable-largefile \
	--with-libtool \
	--enable-shared \
	--enable-static \
	--with-openssl=${STAGING_LIBDIR}/.. \
	--with-gssapi=${STAGING_LIBDIR}/../ \
	--with-gnu-ld \
	--with-dlz-postgres=no \
	--with-dlz-mysql=no \
	--with-dlz-bdb=no \
	--with-dlz-filesystem=yes \
	--with-dlz-stub=yes \
	--with-geoip=no \
	--enable-ipv6 \
	--disable-linux-caps \
	--disable-threads \
	--with-ecdsa=yes \
	--with-gost=no \
	--with-randomdev=no \
	--with-dlz-odbc=no \
	--with-dlz-ldap=${STAGING_LIBDIR}/../ \
	--enable-exportlib \
"

inherit autotools-brokensep update-rc.d systemd useradd pkgconfig

PACKAGECONFIG ?= ""
PACKAGECONFIG[httpstats] = "--with-libxml2,--without-libxml2,libxml2"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--system --home /var/cache/bind --no-create-home \
                       --user-group bind"

INITSCRIPT_NAME = "bind"
INITSCRIPT_PARAMS = "defaults"

SYSTEMD_SERVICE_${PN} = "named.service"

PARALLEL_MAKE = ""

RDEPENDS_${PN} = "python-core"
RDEPENDS_${PN}-dev = ""

do_install_prepend() {
	# clean host path in isc-config.sh before the hardlink created
	# by "make install":
	#   bind9-config -> isc-config.sh
	sed -i -e "s,${STAGING_LIBDIR},${libdir}," ${B}/isc-config.sh
}

do_install_append () {
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

	install -D ${S}/debian/ip-up.d ${D}${sysconfdir}/ppp/ip-up.d/bind9
	install -D ${S}/debian/ip-down.d ${D}${sysconfdir}/ppp/ip-down.d/bind9

	install -D -m 644 ${S}/debian/bind9.ufw.profile ${D}${sysconfdir}/ufw/applications.d/bind9

	install -D -m 644 ${S}/bind9.service ${D}${systemd_system_unitdir}/bind9.service
	install -D -m 644 ${S}/bind9-resolvconf.service ${D}${systemd_system_unitdir}/bind9-resolvconf.service
	install -D -m 644 ${S}/debian/bind9.tmpfile ${D}${libdir}/tmpfiles.d/lwresd.conf
	install -D ${S}/debian/lwresd.service ${D}${systemd_system_unitdir}/lwresd.service
	
	mv ${D}${libdir}/bind9/* ${D}${libdir}/
	rm -r ${D}${libdir}/bind9/

	ln -sf libbind9.so.90.0.9 ${D}${libdir}/libbind9.so
	ln -sf libdns.so.100.2.2 ${D}${libdir}/libdns.so
	ln -sf libisc.so.95.5.0 ${D}${libdir}/libisc.so
	ln -sf libisccc.so.90.0.6 ${D}${libdir}/libisccc.so
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

	# Install script follow Debian
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/debian/bind9.init ${D}${sysconfdir}/init.d/bind9
	install -m 0755 ${S}/debian/lwresd.init ${D}${sysconfdir}/init.d/lwresd		
	
	install -d ${D}${includedir}/bind-export/
	mv ${D}${includedir}/bind9/dns ${D}${includedir}/bind-export/
	mv ${D}${includedir}/bind9/dst ${D}${includedir}/bind-export/
	mv ${D}${includedir}/bind9/isc ${D}${includedir}/bind-export/
	mv ${D}${includedir}/bind9/irs ${D}${includedir}/bind-export/
	mv ${D}${includedir}/bind9/isccfg ${D}${includedir}/bind-export/
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

PACKAGES =+ "${PN}9-host ${PN}9utils dnsutils host libbind-dev \
	libbind-export-dev libbind9-90 libdns-export100 libdns100 \
	libirs-export91 libisc-export95 libisc95 libisccc90 \
	libisccfg-export90 libisccfg90 liblwres90 lwresd \
    "

FILES_${PN}9-host = " \
	${bindir}/host \
    "

FILES_${PN}9utils = " \
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
	${includedir}/bind-export/dns/* \
	${includedir}/bind-export/dst/* \
	${includedir}/bind-export/irs/* \
	${includedir}/bind-export/isc/* \
	${includedir}/bind-export/isccfg \
	${includedir}/bind9/*.h \
	${includedir}/dns/* \
	${includedir}/dst/* \
	${includedir}/isc/* \
	${includedir}/isccc/* \
	${includedir}/isccfg/* \
	${includedir}/lwres/* \
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

PKG_${PN} = "${PN}9" 
