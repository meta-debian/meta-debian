#
# Base recipe: meta/recipes-extended/cronie/cronie_1.4.11.bb
# Base branch: daisy
#

DESCRIPTION = "process scheduling daemon"

PR = "r0"

inherit debian-package

# Fix Makefile to disable stripping
SRC_URI += "file://fix-makefile.patch"

# Internet Systems Consortium License 
LICENSE = "GPL-2+ & ISC"
LIC_FILES_CHKSUM = " \
file://debian/copyright;md5=3de9a1b9c8691191a6bb88b6e4388c62"

inherit autotools-brokensep

DEPENDS += "${@base_contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)}"

# init.d/cron require lsb-base
RDEPENDS_${PN} += "lsb-base"

# There are no debian patch
DEBIAN_PATCH_TYPE = "nopatch"

do_configure() {
	:
}

do_compile_prepend() {
	if [ ${@base_contains('DISTRO_FEATURES', 'pam', 'pam', '', d)} = "pam" ];then
		export PAM_DEFS="-DUSE_PAM"
		export PAM_LIBS="-lpam"
	fi
}

do_install_prepend() {
	install -d ${D}${sbindir}
	install -d ${D}${bindir}
	install -d ${D}${sysconfdir}
	install -d ${D}${mandir}
	install -d ${D}${docdir}
	
}

do_install_append() {
	install -d ${D}${sysconfdir}/cron.d
	install -d ${D}${sysconfdir}/cron.hourly
	install -d ${D}${sysconfdir}/cron.daily
	install -d ${D}${sysconfdir}/cron.weekly
	install -d ${D}${sysconfdir}/cron.monthly
	install -d ${D}${sysconfdir}/init.d
	install -m 644 ${S}/debian/crontab.main ${D}${sysconfdir}/crontab
	install -m 644 ${S}/debian/placeholder ${D}${sysconfdir}/cron.d/.placeholder
	install -m 644 ${S}/debian/placeholder ${D}${sysconfdir}/cron.hourly/.placeholder
	install -m 644 ${S}/debian/placeholder ${D}${sysconfdir}/cron.daily/.placeholder
	install -m 644 ${S}/debian/placeholder ${D}${sysconfdir}/cron.weekly/.placeholder
	install -m 644 ${S}/debian/placeholder ${D}${sysconfdir}/cron.monthly/.placeholder
	install -m 755 ${S}/debian/cron.init ${D}${sysconfdir}/init.d/cron
	
	# Install pam if enable pam feature
	if [ ${@base_contains('DISTRO_FEATURES', 'pam', 'pam', '', d)} = "pam" ];then
		install -d ${D}${sysconfdir}/pam.d
		install -m 755 ${S}/debian/cron.pam ${D}${sysconfdir}/pam.d/cron	
	fi
	
	# Install systemd service if systemd feature is enabled
	if [ ${@base_contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)} = "systemd" ];then
		install -d ${D}${base_libdir}/systemd/system
		install -m 644 ${S}/debian/cron.service ${D}${base_libdir}/systemd/system
	fi	
}

FILES_${PN} += "${base_libdir}/*"
