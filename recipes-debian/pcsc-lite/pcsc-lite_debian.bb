SUMMARY = "Middleware to access a smart card using PC/SC"
DESCRIPTION = "The purpose of PC/SC Lite is to provide a Windows(R) SCard interface \
in a very small form factor for communicating to smart cards and \
smart cards readers."
HOMEPAGE = "http://pcsclite.alioth.debian.org/"

inherit debian-package
PV = "1.8.13"

LICENSE = "BSD-3-Clause & GPLv3+ & MIT & ISC"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=bcfbd85230ac3c586fb294c8b627cf32 \
	file://GPL-3.0.txt;md5=d32239bcb673463ab874e80d47fae504 \
	file://src/sd-daemon.c;beginline=3;endline=25;md5=ca98f47674a446ca9e786ce689b9cea9 \
	file://src/strlcat.c;beginline=3;endline=17;md5=f800c066486863c8a78974152f69b5ed"

inherit autotools pkgconfig

DEPENDS += "systemd flex"

EXTRA_OECONF += "--enable-usbdropdir=${libdir}${DPN}/drivers"
do_install_append() {
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/debian/pcscd.init \
		${D}${sysconfdir}/init.d/pcscd
}
PACKAGES =+ "pcscd"

FILES_pcscd = "${sysconfdir}/init.d/pcscd \
               ${systemd_system_unitdir}/* \
               ${sbindir}/pcscd"

FILES_${PN}-dev += "${bindir}/pcsc-spy \
                    ${libdir}/libpcscspy.*"

RPROVIDES_${PN} += "libpcsclite1"
RPROVIDES_${PN}-dev += "libpcsclite-dev"
RDEPENDS_pcscd += "${PN} lsb-base"
