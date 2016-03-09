SUMMARY = "control and monitor storage systems using S.M.A.R.T."
DESCRIPTION = "The smartmontools package contains two utility programs (smartctl and smartd) \
to control and monitor storage systems using the Self-Monitoring, Analysis and \
Reporting Technology System (S.M.A.R.T.) built into most modern ATA and SCSI \
hard disks. It is derived from the smartsuite package, and includes support \
for ATA/ATAPI-5 disks. It should run on any modern Linux system."
HOMEPAGE = "http://smartmontools.sourceforge.net/"

PR = "r0"
inherit debian-package

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

RDEPENDS_${PN} = "debianutils lsb-base"

inherit autotools systemd

SYSTEMD_SERVICE_${PN} = "smartd.service"

#Follow debian/rules
EXTRA_OECONF = " \
    --with-initscriptdir=no \
    --with-docdir=${docdir}/${DPN} \
    --enable-drivedb \
    --enable-savestates \
    --enable-attributelog \
    --with-savestates=${localstatedir}/lib/${DPN}/smartd. \
    --with-attributelog=${localstatedir}/lib/${DPN}/attrlog. \
    --with-exampledir=${docdir}/${DPN}/examples/ \
    --with-drivedbdir=${localstatedir}/lib/${DPN}/drivedb \
    --with-systemdsystemunitdir=${systemd_system_unitdir} \
    --with-smartdscriptdir=${datadir}/${DPN} \
    --with-smartdplugindir=${sysconfdir}/${DPN}/smartd_warning.d \
    --with-systemdenvfile=${sysconfdir}/default/smartmontools \
"

PACKAGECONFIG ??= " \
    ${@base_contains('DISTRO_FEATURES', 'libcap-ng', 'libcap-ng', '', d)} \
    ${@base_contains('DISTRO_FEATURES', 'selinux', 'selinux', '', d)} \
"
PACKAGECONFIG[libcap-ng] = "--with-libcap-ng=yes,--with-libcap-ng=no,libcap-ng"
PACKAGECONFIG[selinux] = "--with-selinux=yes,--with-selinux=no,libselinux"

do_install_append() {
	install -d ${D}${sysconfdir}/default \
	           ${D}${sysconfdir}/init.d \
	           ${D}${sysconfdir}/${DPN}/run.d

	install ${S}/debian/smartmontools.default ${D}${sysconfdir}/default/smartmontools
	install ${S}/debian/smartmontools.init    ${D}${sysconfdir}/init.d/smartmontools

	# Follow debian/smartmontools.links
	ln -s smartd.service ${D}${systemd_system_unitdir}/smartmontools.service

	# Follow debian/smartmontools.install
	install -m 0755 ${S}/debian/smartd-runner      ${D}${datadir}/${DPN}/
	install -m 0755 ${S}/debian/10mail             ${D}${sysconfdir}/${DPN}/run.d/
	install -m 0755 ${S}/debian/10powersave-notify ${D}${sysconfdir}/${DPN}/run.d/
}

# Follow debian/rules
INITSCRIPT_NAME = "smartmontools"
INITSCRIPT_PARAMS = "start 20 2 3 4 5 . stop 20 1 ."

FILES_${PN} += "${systemd_system_unitdir}"
