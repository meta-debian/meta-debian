#
# base recipe: meta/recipes-connectivity/nfs-utils/nfs-utils_1.2.9.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package

LICENSE = "GPLv2+ & MIT & BSD"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=95f3a93a5c3c7888de623b46ea085a84 \
	file://install-sh;beginline=10;endline=32;md5=b305c58d8bbd3e6dec5f21cd86edec25 \
	file://utils/idmapd/nfs_idmap.h;beginline=6;endline=34;md5=e458358e2a6d47eb6ab729685754a1b1 \
"

# util-linux for libblkid
DEPENDS = "libcap libnfsidmap libevent util-linux sqlite3"
RDEPENDS_nfs-common = "rpcbind bash lsb-base"
RDEPENDS_${PN} = "nfs-common bash lsb-base"
RRECOMMENDS_${PN} = "kernel-module-nfsd"

inherit useradd

USERADD_PACKAGES = "nfs-common"
USERADD_PARAM_nfs-common = "--system  --home-dir /var/lib/nfs \
                              --shell /bin/false --user-group rpcuser"

PARALLEL_MAKE = ""

inherit autotools-brokensep update-rc.d

INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME = "nfs-kernel-server"
INITSCRIPT_PARAMS = "defaults 20 80"

# --enable-uuid is need for cross-compiling
EXTRA_OECONF = "--with-statduser=nobody \
                --enable-libmount-mount \
                --enable-uuid \
                --with-statdpath=/var/lib/nfs/statd \
               "

PACKAGECONFIG ??= "tcp-wrappers nfsv41 \
		${@base_contains('DISTRO_FEATURES', 'ipv6', 'ipv6', '', d)} \
"
PACKAGECONFIG[tcp-wrappers] = "--with-tcp-wrappers,--without-tcp-wrappers,tcp-wrappers"
PACKAGECONFIG[ipv6] = "--enable-ipv6 --with-tirpcinclude=${STAGING_INCDIR}/tirpc,--disable-ipv6,libtirpc,libtirpc"
PACKAGECONFIG[nfsv41] = "--enable-nfsv41,--disable-nfsv41,lvm2"
PACKAGECONFIG[gss] = "--enable-gss,--disable-gss,krb5"

# Make clean needed because the package comes with
# precompiled 64-bit objects that break the build
do_compile_prepend() {
	make clean
}

do_install_append() {
	# Follow debian/rules
	install -d ${D}${sysconfdir}/init.d ${D}${datadir}/bug/nfs-common \
		${D}${datadir}/bug/nfs-utils ${D}${datadir}/bug/nfs-kernel-server
	install -m 0755 ${S}/debian/nfs-kernel-server.init ${D}${sysconfdir}/init.d/nfs-kernel-server
	install -m 0755 ${S}/debian/nfs-common.init ${D}${sysconfdir}/init.d/nfs-common
	install -m 0644 ${S}/debian/nfs-common.bugcontrol ${D}${datadir}/bug/nfs-common/control
	install -m 0755 ${S}/debian/nfs-common.bugscript ${D}${datadir}/bug/nfs-common/script
	install -m 0644 ${S}/debian/nfs-utils.bugcontrol ${D}${datadir}/bug/nfs-utils/control
	install -m 0644 ${S}/debian/nfs-utils.bugpresubj ${D}${datadir}/bug/nfs-utils/presubj
	install -m 0755 ${S}/debian/nfs-kernel-server.bugscript ${D}${datadir}/bug/nfs-kernel-server/script

	# Install configuration files
	install -d ${D}${sysconfdir}/default ${D}${sysconfdir}/request-key.d
	install -m 0644 ${S}/debian/idmapd.conf ${D}${sysconfdir}/
	install -m 0644 ${S}/debian/id_resolver.conf ${D}${sysconfdir}/request-key.d/
	install -m 0644 ${S}/debian/etc.exports ${D}${sysconfdir}/exports
	install -m 0644 ${S}/debian/nfs-common.default ${D}${sysconfdir}/default/nfs-common
	install -m 0644 ${S}/debian/nfs-kernel-server.default ${D}${sysconfdir}/default/nfs-kernel-server

	# Correct file location follow Debian
	( cd ${D}${sbindir} && mv rpc.statd sm-notify showmount ${D}${base_sbindir} )
}

PACKAGES =+ "nfs-common"

FILES_nfs-common = " \
	${sysconfdir}/*/nfs-common ${sysconfdir}/request-key.d \
	${base_sbindir}/* ${sbindir}/blkmapd ${sbindir}/gss* ${sbindir}/mountstats \
	${sbindir}/nfsidmap ${sbindir}/nfsiostat ${sbindir}/nfsstat \
	${sbindir}/rpc.gssd ${sbindir}/rpc.idmapd ${sbindir}/rpc.svcgssd \
	${sbindir}/rpcdebug ${sbindir}/start-statd ${localstatedir}/lib/nfs/state \
	${sysconfdir}/idmapd.conf ${datadir}/bug/nfs-common ${datadir}/bug/nfs-utils \
"
FILES_${PN} += "${datadir}/bug/nfs-kernel-server"

# Change package name follow Debian
PKG_${PN} = "nfs-kernel-server"
RPROVIDES_${PN} += "nfs-kernel-server"

# Keep compatible with meta
RPROVIDES_nfs-common = "${PN}-client"
