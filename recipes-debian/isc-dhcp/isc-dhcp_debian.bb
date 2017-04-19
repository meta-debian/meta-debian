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

inherit debian-package
PV = "4.3.1"

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
	install -m 0755 ${S}/debian/isc-dhcp-relay.init.d ${D}${sysconfdir}/init.d/isc-dhcp-relay

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

PACKAGES =+ "${PN}-client ${PN}-relay ${PN}-common"

FILES_${PN}-relay = " \
	${sbindir}/dhcrelay \
	${sysconfdir}/init.d/isc-dhcp-relay \
    "
FILES_${PN}-client = " \
	${base_sbindir}/dhclient \
	${base_sbindir}/dhclient-script \
	${sysconfdir}/dhcp/dhclient* \
    "
FILES_${PN}-common = "${bindir}/omshell"

RDEPENDS_${PN}-client = "bash"

PKG_${PN} = "${PN}-server"
RPROVIDES_${PN} += "${PN}-server"

pkg_postinst_${PN}() {
	INITCONFFILE="$D${sysconfdir}/default/isc-dhcp-server"

	# We generate several files during the postinst, and we don't want
	#       them to be readable only by root.
	umask 022

	# Generate configuration file if it does not exist, using default values.
	[ -r "${INITCONFFILE}" ] || {
		echo Generating ${INITCONFFILE}... >&2
		cat >${INITCONFFILE} <<'EOFMAGICNUMBER1234'
# Defaults for isc-dhcp-server initscript
# sourced by /etc/init.d/isc-dhcp-server
# installed at /etc/default/isc-dhcp-server by the maintainer scripts

#
# This is a POSIX shell fragment
#

# Path to dhcpd's config file (default: /etc/dhcp/dhcpd.conf).
#DHCPD_CONF=/etc/dhcp/dhcpd.conf

# Path to dhcpd's PID file (default: /var/run/dhcpd.pid).
#DHCPD_PID=/var/run/dhcpd.pid

# Additional options to start dhcpd with.
#       Don't use options -cf or -pf here; use DHCPD_CONF/ DHCPD_PID instead
#OPTIONS=""

# On what interfaces should the DHCP server (dhcpd) serve DHCP requests?
#       Separate multiple interfaces with spaces, e.g. "eth0 eth1".
INTERFACES=""
EOFMAGICNUMBER1234

	mkdir -p $D/${localstatedir}/lib/dhcp
	touch $D/${localstatedir}/lib/dhcp/dhcpd.leases
	touch $D/${localstatedir}/lib/dhcp/dhcpd6.leases
	}
}
pkg_postinst_${PN}-client() {
	mkdir -p $D/${localstatedir}/lib/dhcp
}

pkg_postrm_${PN}() {
	rm -f $D/${localstatedir}/lib/dhcp/dhcpd.leases
	rm -f $D/${localstatedir}/lib/dhcp/dhcpd6.leases

	if ! rmdir $D/${localstatedir}/lib/dhcp 2>/dev/null; then
		echo "Not removing ${localstatedir}/lib/dhcp as it is non-empty."
	fi
}

pkg_postrm_${PN}-client() {
	rm -f $D/${localstatedir}/lib/dhcp/dhclient.leases
	rm -f $D/${localstatedir}/lib/dhcp/dhclient6.leases

	if ! rmdir $D/${localstatedir}/lib/dhcp 2>/dev/null; then
		echo "Not removing ${localstatedir}/lib/dhcp as it is non-empty."
	fi
}
