SUMMARY = "Helper tools for all init systems"
DESCRIPTION = "This package contains helper tools that are necessary for switching between\n\
 the various init systems that Debian contains (e.g. sysvinit, upstart,\n\
 systemd). An example is deb-systemd-helper, a script that enables systemd unit\n\
 files without depending on a running systemd.\n\
 .\n\
 While this package is maintained by pkg-systemd-maintainers, it is NOT\n\
 specific to systemd at all. Maintainers of other init systems are welcome to\n\
 include their helpers in this package."

HOMEPAGE = "https://packages.qa.debian.org/i/init-system-helpers.html"

PR = "r0"
inherit debian-package
PV = "1.22"

LICENSE = "GPLv3+ & BSD"
LIC_FILES_CHKSUM = "\
	file://script/deb-systemd-invoke;beginline=3;endline=30;md5=42d36293e53d929566aef24e3e3b9ef8 \
	file://systemd2init/COPYING;md5=d32239bcb673463ab874e80d47fae504"

do_install() {
	install -d ${D}${bindir}
	install -d ${D}${datadir}/dh-systemd
	install -d ${D}${datadir}/perl5/Debian/Debhelper/Sequence
	install -d ${D}${datadir}/debhelper

	#follow debian/dh-systemd.install and debian/init-system-helpers.install
	install -m 0755 script/* ${D}${bindir}
	install -m 0755 systemd2init/systemd2init ${D}${bindir}
	install -m 0644 systemd2init/skeleton.* ${D}${datadir}/dh-systemd
	install -m 0644 lib/Debian/Debhelper/Sequence/systemd.pm \
		${D}${datadir}/perl5/Debian/Debhelper/Sequence
	cp -r autoscripts ${D}${datadir}/debhelper
}

PACKAGES =+ "dh-systemd"
FILES_dh-systemd = "\
	${bindir}/dh_systemd_enable ${bindir}/dh_systemd_start \
	${bindir}/systemd2init ${datadir}/debhelper \
	${datadir}/dh-systemd ${datadir}/perl5"

RDEPENDS_${PN} = "perl-base"
