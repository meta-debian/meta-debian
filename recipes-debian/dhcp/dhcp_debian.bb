#
# base recipe: meta/recipes-connectivity/dhcp/dhcp_4.3.2.bb
# base branch: jethro
#

SECTION = "console/network"
SUMMARY = "Internet Software Consortium DHCP package"
DESCRIPTION = "DHCP (Dynamic Host Configuration Protocol) is a protocol \
which allows individual devices on an IP network to get their own \
network configuration information from a server.  DHCP helps make it \
easier to administer devices."

HOMEPAGE = "http://www.isc.org/"

PR = "r0"
inherit debian-package
DPN = "isc-dhcp"

LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://LICENSE;md5=cd834c80467e962621bf1b82ee363c34"

DEPENDS = "openssl bind"

# define-macro-_PATH_DHCPD_CONF-and-_PATH_DHCLIENT_CON.patch
# 	Define this if you want the dhcpd.conf file to go somewhere other than
# 	the default location.   By default, it goes in /etc/dhcpd.conf

# link-with-lcrypto.patch
# 	-lcrypto check was removed and we compile static libraries
# 	from bind that are linked to libcrypto

# fixsepbuild.patch
#	Fix out of tree builds
SRC_URI += " \
	file://define-macro-_PATH_DHCPD_CONF-and-_PATH_DHCLIENT_CON.patch \
	file://dhcp-3.0.3-dhclient-dbus.patch;striplevel=0 \
	file://link-with-lcrypto.patch \
	file://dhclient-script-drop-resolv.conf.dhclient.patch \
	file://replace-ifconfig-route.patch \
	file://fixsepbuild.patch \
"

inherit autotools systemd

# link correct to path of library
CFLAGS =+ " -I${STAGING_INCDIR}/bind-export/ "

TARGET_CFLAGS += "-D_GNU_SOURCE"
EXTRA_OECONF = "--with-srv-lease-file=${localstatedir}/lib/dhcp/dhcpd.leases \
                --with-srv6-lease-file=${localstatedir}/lib/dhcp/dhcpd6.leases \
                --with-cli-lease-file=${localstatedir}/lib/dhcp/dhclient.leases \
                --with-cli6-lease-file=${localstatedir}/lib/dhcp/dhclient6.leases \
                --with-libbind=${STAGING_LIBDIR}/ \
               "

do_install_append () {
	install -d ${D}${sysconfdir}/dhcp/dhclient-enter-hooks.d/
	install -d ${D}${sysconfdir}/dhcp/dhclient-exit-hooks.d/
	install -m 0644 ${S}/debian/rfc3442-classless-routes.linux \
			${D}${sysconfdir}/dhcp/dhclient-exit-hooks.d/rfc3442-classless-routes
	install -m 0644 ${S}/debian/debug ${D}${sysconfdir}/dhcp/dhclient-enter-hooks.d/
	install -m 0644 ${S}/debian/debug ${D}${sysconfdir}/dhcp/dhclient-exit-hooks.d/
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${sysconfdir}/dhcp
	install -m 0755 ${S}/debian/isc-dhcp-server.init.d ${D}${sysconfdir}/init.d/isc-dhcp-server
	
	install -m 0755 ${S}/contrib/dhcp-lease-list.pl ${D}${sbindir}/dhcp-lease-list
	rm -f ${D}${sysconfdir}/dhclient.conf*
	rm -f ${D}${sysconfdir}/dhcpd.conf*
	install -m 0644 ${S}/debian/dhclient.conf ${D}${sysconfdir}/dhcp/dhclient.conf
	install -m 0644 ${S}/debian/dhcpd.conf ${D}${sysconfdir}/dhcp/dhcpd.conf

	install -d ${D}${base_sbindir}/
	if [ "${sbindir}" != "${base_sbindir}" ]; then
		mv ${D}${sbindir}/dhclient ${D}${base_sbindir}/
	fi
	install -m 0755 ${S}/client/scripts/linux ${D}${base_sbindir}/dhclient-script
}

PACKAGES =+ "isc-dhcp-server isc-dhcp-server-config isc-dhcp-client isc-dhcp-relay isc-dhcp-common"

FILES_isc-dhcp-server = " \
	${sbindir}/dhcpd \
	${sysconfdir}/init.d/isc-dhcp-server \
	${sbindir}/dhcp-lease-list \
	${sysconfdir}/dhcp/dhcpd.conf \
    "

FILES_isc-dhcp-relay = " \
	${sbindir}/dhcrelay \
	${sysconfdir}/init.d/isc-dhcp-relay \
    "

FILES_isc-dhcp-client = " \
	${base_sbindir}/dhclient \
	${base_sbindir}/dhclient-script \
	${sysconfdir}/dhcp/dhclient* \
    "
RDEPENDS_isc-dhcp-client = "bash"

FILES_isc-dhcp-common = "${bindir}/omshell"

PKG_${PN}-dev = "isc-${PN}-dev"
pkg_postinst_isc-dhcp-server() {
	mkdir -p $D/${localstatedir}/lib/dhcp
	touch $D/${localstatedir}/lib/dhcp/dhcpd.leases
	touch $D/${localstatedir}/lib/dhcp/dhcpd6.leases
}

pkg_postinst_isc-dhcp-client() {
	mkdir -p $D/${localstatedir}/lib/dhcp
}

pkg_postrm_isc-dhcp-server() {
	rm -f $D/${localstatedir}/lib/dhcp/dhcpd.leases
	rm -f $D/${localstatedir}/lib/dhcp/dhcpd6.leases

	if ! rmdir $D/${localstatedir}/lib/dhcp 2>/dev/null; then
		echo "Not removing ${localstatedir}/lib/dhcp as it is non-empty."
	fi
}

pkg_postrm_isc-dhcp-client() {
	rm -f $D/${localstatedir}/lib/dhcp/dhclient.leases
	rm -f $D/${localstatedir}/lib/dhcp/dhclient6.leases

	if ! rmdir $D/${localstatedir}/lib/dhcp 2>/dev/null; then
		echo "Not removing ${localstatedir}/lib/dhcp as it is non-empty."
	fi
}
