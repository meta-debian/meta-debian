SUMMARY = "name server information handler"
DESCRIPTION = "Resolvconf is a framework for keeping up to date the system's \
information about name servers. It sets itself up as the intermediary \
between programs that supply this information (such as ifup and \
ifdown, DHCP clients, the PPP daemon and local name servers) and \
programs that use this information (such as DNS caches and resolver \
libraries)."
HOMEPAGE = "http://alioth.debian.org/projects/resolvconf/"

inherit debian-package
PV = "1.76.1"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=c93c0550bd3173f4504b2cbd8991e50b"

inherit allarch

do_install() {
	# According to debian/rules
	install -d ${D}${sysconfdir} ${D}${base_sbindir} \
	           ${D}${base_libdir}/${DPN} \
	           ${D}${datadir}/${DPN} \
	           ${D}${sysconfdir}/network/if-up.d \
	           ${D}${sysconfdir}/network/if-down.d \
	           ${D}${localstatedir}/lib/${DPN}

	cp -a ${S}/etc ${D}/
	install -p --owner=root --group=root --mode=0755 ${S}/bin/resolvconf ${D}${base_sbindir}
	install -p --owner=root --group=root --mode=0755 ${S}/bin/list-records ${D}${base_libdir}/${DPN}/
	install -p --owner=root --group=root --mode=0755 ${S}/bin/dump-debug-info ${D}${datadir}/${DPN}/

	install -D -m 0755 ${S}/debian/resolvconf.init ${D}${sysconfdir}/init.d/resolvconf
	install -D -m 0644 ${S}/debian/resolvconf.upstart \
	                       ${D}${sysconfdir}/init/resolvconf.conf
	install -D -m 0644 ${S}/debian/resolvconf.service \
	                       ${D}${systemd_system_unitdir}/resolvconf.service
	install -D -m 0755 ${S}/debian/resolvconf.000resolvconf.ppp.ip-down \
	                       ${D}${sysconfdir}/ppp/ip-down.d/000resolvconf
	install -D -m 0755 ${S}/debian/resolvconf.000resolvconf.ppp.ip-up \
	                       ${D}${sysconfdir}/ppp/ip-up.d/000resolvconf
	install -D -m 0755 ${S}/debian/resolvconf.resolvconf.if-down \
	                       ${D}${sysconfdir}/network/if-down.d/resolvconf
	install -D -m 0755 ${S}/debian/resolvconf.000resolvconf.if-up \
	                       ${D}${sysconfdir}/network/if-up.d/000resolvconf
	install -D -m 0644 ${S}/README ${D}${docdir}/${DPN}/README
	install -D -m 0644 ${S}/man/resolvconf.8 ${D}${mandir}/man8/resolvconf.8
	install -D -m 0644 ${S}/man/interface-order.5 ${D}${mandir}/man5/interface-order.5
}

FILES_${PN} += "${base_libdir}"

RDEPENDS_${PN} += "lsb-base"
