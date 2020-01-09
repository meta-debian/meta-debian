# base recipe: meta-openembedded/meta-networking/recipes-filter/nftables/nftables_0.9.0.bb
# base branch: warrior

SUMMARY = "Netfilter Tables userspace utillites"
SECTION = "net"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d1a78fdd879a263a5e0b42d1fc565e79"

inherit debian-package
require recipes-debian/sources/nftables.inc

DEPENDS = "libmnl libnftnl readline gmp bison-native"

inherit autotools manpages pkgconfig

PACKAGECONFIG ?= ""
PACKAGECONFIG[manpages] = "--enable--man-doc, --disable-man-doc"

do_install_append() {
	install -m 0755 ${S}/debian/nftables.conf ${D}${sysconfdir}/nftables.conf

	install -d ${D}${systemd_system_unitdir}
	install -m 0644 ${S}/debian/nftables.service ${D}${systemd_system_unitdir}/nftables.service

	rm -rf ${D}${sysconfdir}/nftables

	install -d ${D}${docdir}/nftables/examples
	install -m 0755 ${S}/debian/examples/*.nft ${D}${docdir}/nftables/examples
	install -m 0755 ${S}/files/examples/*.nft ${D}${docdir}/nftables/examples

	install -d ${D}${docdir}/nftables/examples/sysvinit
	install -m 0644 ${S}/debian/examples/sysvinit/nftables.init ${D}${docdir}/nftables/examples/sysvinit
}

PACKAGES = "${PN} ${PN}-dbg ${PN}-dev ${PN}-src"

FILES_${PN} += "${systemd_system_unitdir}/nftables.service \
                ${docdir}/nftables/examples/*.nft \
                ${docdir}/nftables/examples/sysvinit/* \
                "

ASNEEDED = ""

RRECOMMENDS_${PN} += "kernel-module-nf-tables"
