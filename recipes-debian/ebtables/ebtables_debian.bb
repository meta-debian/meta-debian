SUMMARY = "Ethernet bridge frame table administration"
DESCRIPTION = "Ebtables is used to set up, maintain, and inspect the tables of \
 Ethernet frame rules in the Linux kernel. It is analogous to iptables, \
 but operates at the MAC layer rather than the IP layer."
HOMEPAGE = "http://ebtables.sourceforge.net"

PR = "r0"
inherit debian-package
PV = "2.0.10.4"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=53b4a999993871a28ab1488fdbd2e73e"

inherit autotools-brokensep
CFLAGS += "-fstack-protector-all"
EXTRA_OEMAKE = " \
	'LIBDIR=${base_libdir}/${PN}' \
	'BINDIR=${base_sbindir}' \
	'CC=${CC}' \
	'CFLAGS=${CFLAGS}' \
	'LDFLAGS=${LDFLAGS}' \
	"
do_install_append() {
	# base on debian/rules
	mv ${D}${sysconfdir}/default/ebtables-config \
	   ${D}${sysconfdir}/default/ebtables
	rm -rf ${D}${base_sbindir}/ebtables-save \
	       ${D}${base_sbindir}/ebtables-restore
    rm -f ${D}${sysconfdir}/init.d/ebtables
    install -m 0755 ${S}/debian/ebtables.init ${D}${sysconfdir}/init.d/ebtables
}
RRECOMMENDS_${PN} += "iptables kmod"
# Avoid a parallel build problem
PARALLEL_MAKE = ""
FILES_${PN} += "${base_libdir}/${PN}/*"
FILES_${PN}-dbg += "${base_libdir}/${PN}/.debug"
