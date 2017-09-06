#
# base recipe: meta/recipes-extended/logrotate/logrotate_3.8.7.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "3.8.7"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=18810669f13b87348459e611d31ab760"

DEPENDS="coreutils popt"

# Avoid error:
#	"logrotate.c:2180:40: error: 'VERSION' undeclared (first use in this function)"
# "-e" from EXTRA_OEMAKE makes the CFLAGS from the env overrides CFLAGS from logrotate/Makefile.
EXTRA_OEMAKE = ""

do_install(){
	oe_runmake PREFIX=${D} MANDIR=${mandir} install

	install -d ${D}${sysconfdir}/cron.daily
	install -m 0644 ${S}/debian/logrotate.conf ${D}${sysconfdir}
	install -m 0755 ${S}/debian/cron.daily ${D}${sysconfdir}/cron.daily/logrotate
	install -d ${D}${localstatedir}/lib/logrotate
}
