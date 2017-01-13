#
# Base recipe: recipes-core/ifupdown/ifupdown_0.7.48.1.bb
# Base branch: jethro
#

SUMMARY = "ifupdown: basic ifup and ifdown used by initscripts"
DESCRIPTION = "High level tools to configure network interfaces \
This package provides the tools ifup and ifdown which may be used to \
configure (or, respectively, deconfigure) network interfaces, based on \
the file /etc/network/interfaces."

PR = "r0"

inherit debian-package
PV = "0.7.53.1"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

do_compile() {
	chmod a+rx *.pl *.sh
	oe_runmake 'CC=${CC}' "CFLAGS=${CFLAGS} -Wall -W -D'IFUPDOWN_VERSION=\"${PV}\"'"
}

# Install file follow Debian
do_install() {
	install -d ${D}${sysconfdir}/default
	install -d ${D}${sysconfdir}/init
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${sysconfdir}/network/if-down.d
	install -d ${D}${sysconfdir}/network/if-post-down.d
	install -d ${D}${sysconfdir}/network/if-pre-up.d
	install -d ${D}${sysconfdir}/network/if-up.d
	install -d ${D}${sysconfdir}/network/interfaces.d
	install -d ${D}${base_libdir}/${PN}
	install -d ${D}${base_sbindir}
	install -d ${D}${mandir}/man8
	install -d ${D}${mandir}/man5
	install -d ${D}${docdir}
	install -d ${D}${datadir}/lintian/overrides
	
	install -m 0644 ${S}/debian/networking.defaults \
			${D}${sysconfdir}/default/networking
	install -m 0644 ${S}/debian/ifupdown.network-interface-container.upstart \
			${D}${sysconfdir}/init/network-interface-container.conf
	install -m 0644 ${S}/debian/ifupdown.network-interface-security.upstart \
			${D}${sysconfdir}/init/network-interface-security.conf
	install -m 0644 ${S}/debian/ifupdown.network-interface.upstart \
			${D}${sysconfdir}/init/network-interface.conf
	install -m 0644 ${S}/debian/ifupdown.networking.upstart \
			${D}${sysconfdir}/networking.conf
	install -m 0755 ${S}/debian/networking.init \
			${D}${sysconfdir}/init.d/networking
	install -m 0755 ${S}/debian/ifupdown.upstart.if-down \
			${D}${sysconfdir}/network/if-down.d/upstart
	install -m 0755 ${S}/debian/ifupdown.upstart.if-up \
			${D}${sysconfdir}/network/if-up.d
	install -m 0755 ${S}/settle-dad.sh ${D}${base_libdir}/${PN}
	install -m 0755 ${S}/ifup ${D}${base_sbindir}
	ln -sf ifup ${D}${base_sbindir}/ifdown 	
	ln -sf ifup ${D}${base_sbindir}/ifquery

	install -m 0644 ${S}/debian/ifupdown.lintian-overrides \
			${D}${datadir}/lintian/overrides/ifupdown
	install -m 0644 ifup.8 ${D}${mandir}/man8
	install -m 0644 interfaces.5 ${D}${mandir}/man5
	cd ${D}${mandir}/man8 && ln -s ifup.8 ifdown.8
}

FILES_${PN} += "${base_libdir}"
FILES_${PN}-doc += "${datadir}"

# Add update-alternatives definitions
inherit update-alternatives

ALTERNATIVE_PRIORITY="100"
ALTERNATIVE_${PN} = "ifup ifdown"
ALTERNATIVE_LINK_NAME[ifup] = "${base_sbindir}/ifup"
ALTERNATIVE_LINK_NAME[ifdown] = "${base_sbindir}/ifdown"

