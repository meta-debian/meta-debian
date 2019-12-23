SUMMARY = "Random number generator daemon"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEBIAN_QUILT_PATCHES = ""

inherit debian-package
require recipes-debian/sources/rng-tools.inc

SRC_URI += "file://fix-security-format.patch \
            file://avoid-ar-warning-messages.patch \
            file://0002-Add-argument-to-control-the-libargp-dependency.patch \
            "

FILESPATH_append = ":${COREBASE}/meta/recipes-support/rng-tools/rng-tools"
SRC_URI += "file://rngd.service"

inherit autotools update-rc.d systemd pkgconfig

do_install_append() {
	# Only install the init script when 'sysvinit' is in DISTRO_FEATURES.
	if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
		install -d "${D}${sysconfdir}/init.d"
		install -m 0755 ${S}/debian/rng-tools.init ${D}${sysconfdir}/init.d/rng-tools
		sed -i -e 's,/etc/,${sysconfdir}/,' -e 's,/usr/sbin/,${sbindir}/,' ${D}${sysconfdir}/init.d/rng-tools

		install -d "${D}${sysconfdir}/default"
		install -m 0644 ${S}/debian/rng-tools.default ${D}${sysconfdir}/default/rng-tools
	fi

	install -d "${D}${sysconfdir}/logcheck/ignore.d.server"
	install -m 0644 ${S}/debian/logcheck.ignore ${D}${sysconfdir}/logcheck/ignore.d.server/rng-tools

	install -d "${D}${sysconfdir}/logcheck/violations.ignore.d"
	install -m 0644 ${S}/debian/logcheck.ignore ${D}${sysconfdir}/logcheck/violations.ignore.d/rng-tools

	if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
		install -d ${D}${systemd_unitdir}/system
		install -m 0644 ${WORKDIR}/rngd.service ${D}${systemd_unitdir}/system
		sed -i -e 's,@SBINDIR@,${sbindir},g' ${D}${systemd_unitdir}/system/rngd.service
	fi
}

FILES_${PN} += "${datadir}"

INITSCRIPT_NAME = "${PN}"
INITSCRIPT_PARAMS = "start 03 2 3 4 5 . stop 30 0 6 1 ."

SYSTEMD_SERVICE_${PN} = "rngd.service"

