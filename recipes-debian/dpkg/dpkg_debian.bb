#
# base recipe: meta/recipes-devtools/dpkg/dpkg_1.17.4.bb
# base branch: daisy
#

require dpkg.inc
PR = "${INC_PR}.0"

inherit systemd

do_install_append () {
	rm ${D}${bindir}/update-alternatives
	if [ "${PN}" = "dpkg-native" ]; then
		sed -i -e 's|^#!.*${bindir}/perl-native.*/perl|#!/usr/bin/env nativeperl|' ${D}${bindir}/dpkg-*
	else
		sed -i -e 's|^#!.*${bindir}/perl-native.*/perl|#!/usr/bin/env perl|' ${D}${bindir}/dpkg-*
	fi

	if ${@base_contains('DISTRO_FEATURES','sysvinit','false','true',d)};then
		install -d ${D}${systemd_unitdir}/system
		install -m 0644 ${WORKDIR}/dpkg-configure.service ${D}${systemd_unitdir}/system/
		sed -i -e 's,@BASE_BINDIR@,${base_bindir},g' \
			-e 's,@SYSCONFDIR@,${sysconfdir},g' \
			-e 's,@BINDIR@,${bindir},g' \
			-e 's,@SYSTEMD_UNITDIR@,${systemd_unitdir},g' \
			${D}${systemd_unitdir}/system/dpkg-configure.service
	fi

	# Install configuration files and links follow Debian
	install -d ${D}${sysconfdir}/cron.daily
	install -d ${D}${sysconfdir}/logrotate.d
	install -m 0644 ${S}/debian/dpkg.cfg ${D}${sysconfdir}/${DPN}/
	install -m 0755 ${S}/debian/dpkg.cron.daily ${D}${sysconfdir}/cron.daily/
	install -m 0644 ${S}/debian/dpkg.logrotate ${D}${sysconfdir}/logrotate.d/

	ln -s ../bin/dpkg-divert ${D}${sbindir}/dpkg-divert
	ln -s ../bin/dpkg-statoverride ${D}${sbindir}/dpkg-statoverride
}

PACKAGES += "${PN}-perl"
FILES_${PN}-perl = "${libdir}/perl"

DEBIANNAME_${PN}-perl = "lib${PN}-perl"
