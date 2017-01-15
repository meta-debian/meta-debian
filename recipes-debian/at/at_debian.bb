#
# base recipe: meta/recipes-extended/at/at_3.1.14.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "3.1.16"

DEBIAN_PATCH_TYPE = "nopatch"

LICENSE = "GPLv2+ & GPLv3+ & ISC"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=4325afd396febcb659c36b49533135d4 \
	file://posixtm.h;endline=23;md5=3a079c253d1eeeeb71bda252cce05939 \
	file://parsetime.pl;endline=17;md5=7c22a480590f3d00f70aef2c2c864d97 \
"

DEPENDS = "flex flex-native \
           ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)}"

RDEPENDS_${PN} = "${@bb.utils.contains('DISTRO_FEATURES', 'pam', '${PAM_DEPS}', '', d)} \
"

PAM_DEPS = "libpam libpam-runtime libpam-modules"

RCONFLICTS_${PN} = "atd"
RREPLACES_${PN} = "atd"

# fix_parallel_build_error.patch:
#	Avoid "fatal error: y.tab.h: No such file or directory"
SRC_URI += "file://fix_parallel_build_error.patch"

# Configure follow debian/rules
EXTRA_OECONF += " \
	--with-loadavg_mx=1.5 \
	--with-daemon_username=root \
	--with-daemon_groupname=root \
	--with-jobdir=${localstatedir}/spool/at/jobs \
	--with-atspool=${localstatedir}/spool/at/spool \
	--with-systemdsystemunitdir=${systemd_unitdir}/system \
	ac_cv_header_security_pam_appl_h=${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'yes', 'no', d)} \
	SENDMAIL=${sbindir}/sendmail \
"

inherit autotools-brokensep systemd

SYSTEMD_SERVICE_${PN} = "atd.service"

# Follow debian/rules
do_install () {
	oe_runmake IROOT='${D}' mandir='${mandir}' docdir='${docdir}' install

	# Remove changelog and copyright:
	rm ${D}${docdir}/at/Copyright
	rm ${D}${docdir}/at/ChangeLog

	rm ${D}${sbindir}/atrun
	rm ${D}${mandir}/man8/atrun.8

	# Install init script
	mkdir -p ${D}${sysconfdir}/init.d
	install ${S}/rc ${D}${sysconfdir}/init.d/atd
	chmod a+x ${D}${sysconfdir}/init.d/atd

	if [ "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)}" = "pam" ]; then
		install -D -m 0644 ${S}/pam.conf ${D}${sysconfdir}/pam.d/atd
	fi
}
