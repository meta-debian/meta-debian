PR = "r0"

inherit debian-package
PV = "0.2.1"

LICENSE = "BSD"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=b46486e4c4a416602693a711bb5bfa39 \
	file://src/rpcinfo.c;beginline=1;endline=27;md5=f8a8cd2cb25ac5aa16767364fb0e3c24 \
"

DEPENDS = "libtirpc"
RDEPENDS_${PN} = "lsb-base"

inherit autotools pkgconfig

# Configure folllow debian/rules
EXTRA_OECONF = "--enable-warmstarts --with-statedir=/run/rpcbind --with-rpcuser=root"

PACKAGECONFIG ??= "tcp-wrappers"
PACKAGECONFIG[tcp-wrappers] = "--enable-libwrap,--disable-libwrap,tcp-wrappers"

do_install_append(){
	# Follow debian/rules
	install -d ${D}${sbindir} ${D}${base_sbindir} ${D}${sysconfdir}/insserv.conf.d
	mv ${D}${bindir}/rpcbind ${D}${base_sbindir}
	mv ${D}${bindir}/rpcinfo ${D}${sbindir}
	rm -r ${D}${bindir}
	install -m 0644 ${S}/debian/insserv.conf ${D}${sysconfdir}/insserv.conf.d/rpcbind

	# Install init scripts for rpcbind
	install -d ${D}${sysconfdir}/init.d ${D}${sysconfdir}/init
	install -m 0755 ${S}/debian/init.d ${D}${sysconfdir}/init.d/rpcbind
	install -m 0644 ${S}/debian/rpcbind.portmap-wait.upstart ${D}${sysconfdir}/init/portmap-wait.conf
	install -m 0644 ${S}/debian/rpcbind.rpcbind-boot.upstart ${D}${sysconfdir}/init/rpcbind-boot.conf
	install -m 0644 ${S}/debian/rpcbind.upstart ${D}${sysconfdir}/init/rpcbind.conf
}

# On Debian Jessie, portmap is provided by rpcbind
RCONFLICTS_${PN} = "portmap"
RPROVIDES_${PN} = "portmap"
