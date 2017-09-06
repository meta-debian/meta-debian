#
# Base recipe: meta/recipes-extended/sysstat/sysstat_10.2.1.bb
# Base branch: daisy
#

SUMMARY = "System performance tools"
DESCRIPTION = "The sysstat utilities are a collection of performance monitoring tools for Linux."
HOMEPAGE = "http://sebastien.godard.pagesperso-orange.fr/"

PR = "r0"

inherit debian-package autotools-brokensep gettext
PV = "11.0.1"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=8ca43cbc842c2336e835926c2166c28b"

# --disable-yesterday:
# sa2 fails to use "-d yesterday", which is not supported by busybox date
EXTRA_OECONF += " \
		--disable-yesterday \
		--disable-man-group \
		--disable-compress-manpg \
		--enable-copy-only \
		sa_lib_dir=${libdir}/${PN} \
		sa_di=${localstatedir}/log/${PN} \
		conf_dir=${sysconfdir}/${PN}"

# Install files follow deian/rules
do_install_append() {
	mkdir -p -m 755 ${D}${libdir}/sysstat
	install -m 0755 ${S}/contrib/isag/isag ${D}${bindir}/isag
	install -m 0644 ${S}/contrib/isag/isag.1 ${D}${mandir}/man1
	install -d ${D}${datadir}/applications
	install -m 0644 ${S}/debian/isag.desktop ${D}${datadir}/applications
	install -m 0755 ${S}/debian/debian-sa1 ${D}${libdir}/${DPN}
	mv ${D}${bindir}/sar ${D}${bindir}/sar.sysstat
	mv ${D}${mandir}/man1/sar.1 ${D}${mandir}/man1/sar.sysstat.1
	rm -rf ${D}${docdir}
	install -d ${D}${sysconfdir}/cron.d
	install -m 0644 ${S}/debian/sysstat.cron.d ${D}${sysconfdir}/cron.d/sysstat
	install -d ${D}${sysconfdir}/cron.daily
	install -m 0755 ${S}/debian/sysstat.cron.daily ${D}${sysconfdir}/cron.daily/sysstat
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/debian/sysstat.init.d ${D}${sysconfdir}/init.d/sysstat
}


# Ship packages follow debian
PACKAGES =+ "isag isag-doc"
FILES_isag += "${bindir}/isag"
FILES_isag-doc += "${datadir}/applications \
		   ${mandir}/man1/isag.1 \
		   ${datadir}/menu/"
