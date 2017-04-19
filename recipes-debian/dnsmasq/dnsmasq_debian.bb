SUMMARY = "Small caching DNS proxy and DHCP/TFTP server"
DESCRIPTION = "\
 Dnsmasq is a lightweight, easy to configure, DNS forwarder and DHCP \
 server. It is designed to provide DNS and optionally, DHCP, to a \
 small network. It can serve the names of local machines which are \
 not in the global DNS. The DHCP server integrates with the DNS \
 server and allows machines with DHCP-allocated addresses \
 to appear in the DNS with names configured either in each host or \
 in a central configuration file. Dnsmasq supports static and dynamic \
 DHCP leases and BOOTP/TFTP for network booting of diskless machines."

PR = "r0"
inherit debian-package
PV = "2.72"

LICENSE = "GPLv2 | GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3 \
                    file://COPYING-v3;md5=d32239bcb673463ab874e80d47fae504"

inherit autotools-brokensep

# We use the version string in contents of the VERSION file to build into a binary.
# instead of use "git describe" command to get version, this command failed when
# we don't have any tags in git repo
SRC_URI += "file://Fix-get-version-of-dnsmasq_debian.patch"

# Debian's source code isn't contains patch file
DEBIAN_PATCH_TYPE = "nopatch"

EXTRA_OEMAKE += "PREFIX=${prefix}"
inherit useradd

# Base on debian/rules
do_install_append() {
	oe_runmake -C ${S}/contrib/wrt DESTDIR=${D} PREFIX=${prefix}
	install -m 755 \
	        -d ${D}${sysconfdir}/init.d \
	        -d ${D}${sysconfdir}/dnsmasq.d \
	        -d ${D}${sysconfdir}/resolvconf/update.d \
	        -d ${D}${libdir}/resolvconf/dpkg-event.d \
	        -d ${D}${sysconfdir}/default \
	        -d ${D}${systemd_system_unitdir} \
	        -d ${D}${sysconfdir}/insserv.conf.d \
	        -d ${D}${sysconfdir}/dbus-1/system.d \
	        -d ${D}${bindir} \
	        -d ${D}${datadir}/${PN}-base

	install -m 755 ${S}/debian/init \
	               ${D}${sysconfdir}/init.d/dnsmasq
	install -m 755 ${S}/debian/resolvconf \
	               ${D}${sysconfdir}/resolvconf/update.d/dnsmasq
	install -m 755 ${S}/debian/resolvconf-package \
	               ${D}${libdir}/resolvconf/dpkg-event.d/dnsmasq
	install -m 644 ${S}/debian/default \
	               ${D}${sysconfdir}/default/dnsmasq
	install -m 644 ${S}/dnsmasq.conf.example \
	               ${D}${sysconfdir}/dnsmasq.conf
	install -m 644 ${S}/debian/readme.dnsmasq.d \
	               ${D}${sysconfdir}/dnsmasq.d/README
	install -m 644 ${S}/debian/systemd.service \
	               ${D}${systemd_system_unitdir}/dnsmasq.service
	install -m 644 ${S}/debian/insserv \
	               ${D}${sysconfdir}/insserv.conf.d/dnsmasq
	install -m 644 ${S}/trust-anchors.conf \
	               ${D}${datadir}/${PN}-base/
	install -m 644 ${S}/debian/dbus.conf \
	               ${D}${sysconfdir}/dbus-1/system.d/dnsmasq.conf
	install -m 755 ${S}/contrib/wrt/dhcp_release \
	               ${D}${bindir}/dhcp_release
	install -m 755 ${S}/contrib/wrt/dhcp_lease_time \
	               ${D}${bindir}/dhcp_lease_time
}
# Base on debian/dnsmasq-base.postinst
USERADD_PACKAGES = "${PN}-base"
USERADD_PARAM_${PN}-base = "-r --home /var/lib/misc \
                              --no-create-home dnsmasq \
                              "
pkg_postinst_${PN}-base() {
	# Make the directory where we keep the pid file - this
	# has to be owned by "dnsmasq" so that the file can be unlinked.
	# This is only actually used by the dnsmasq binary package, not
	# dnsmasq-base, but it's much easier to create it here so that
	# we don't have synchronisation issues with the creation of the
	# dnsmasq user.
	if [ ! -d $D${localstatedir}/run/dnsmasq ]; then
		mkdir $D${localstatedir}/run/dnsmasq
		chown dnsmasq:nogroup $D${localstatedir}/run/dnsmasq
	fi
}
PACKAGES =+ "${PN}-base ${PN}-utils"
FILES_${PN}-base = "\
	${sysconfdir}/dbus-1/system.d/dnsmasq.conf \
	${sbindir}/dnsmasq \
	${datadir}/${PN}-base/trust-anchors.conf \
	"
FILES_${PN}-utils = "${bindir}/*"
FILES_${PN} += "${libdir}/* ${systemd_system_unitdir}"

RDEPENDS_${PN} += "${PN}-base netbase init-system-helpers"
RDEPENDS_${PN}-base += "adduser"
