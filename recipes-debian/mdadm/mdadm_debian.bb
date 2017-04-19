#
# base recipe: meta/recipes-extended/mdadm/mdadm_3.3.4.bb
# base branch: jethro
#

SUMMARY = "tool to administer Linux MD arrays (software RAID)"
DESCRIPTION = "The mdadm utility can be used to create, manage, and monitor MD \
(multi-disk) arrays for software RAID or multipath I/O."
HOMEPAGE = "http://neil.brown.name/blog/mdadm"

inherit debian-package
PV = "3.3.2"

# Some files are GPLv2+ while others are GPLv2.
LICENSE = "GPLv2 & GPLv2+"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
    file://mdmon.c;beginline=4;endline=18;md5=af7d8444d9c4d3e5c7caac0d9d34039d \
    file://mdadm.h;beglinlne=4;endline=22;md5=462bc9936ac0d3da110191a3f9994161 \
"

# mdadm-3.2.2_fix_for_x32.patch:
#     Remove hardcoding CC's definition in the Makefile,
#     because it make all the gcc parameters set by tune settings are lost.
SRC_URI += " \
    file://mdadm-3.2.2_fix_for_x32.patch \
    file://run-ptest \
"

inherit autotools-brokensep

EXTRA_OEMAKE_append = " DEBIAN=yes"
EXTRA_OEMAKE_append_task-install = " STRIP=''"

do_compile() {
	# Base on debian/rules
	oe_runmake CONFFILE=${sysconfdir}/mdadm/mdadm.conf CONFFILE2=${sysconfdir}/mdadm.conf
}

do_install() {
	oe_runmake install install-systemd "DESTDIR=${D}"

	ln -s /dev/null ${D}${systemd_system_unitdir}/mdadm.service
	ln -s /dev/null ${D}${systemd_system_unitdir}/mdadm-waitidle.service

	install -d ${D}${sysconfdir}/mdadm
	install -Dm0755 ${S}/debian/initramfs/hook \
	        ${D}${datadir}/initramfs-tools/hooks/mdadm
	install -Dm0755 ${S}/debian/initramfs/script.local-top \
	        ${D}${datadir}/initramfs-tools/scripts/local-top/mdadm
	install -Dm0644 ${S}/debian/mdadm.modules \
	        ${D}${sysconfdir}/modprobe.d/mdadm.conf

	install -Dm0755 ${S}/debian/mkconf ${D}${datadir}/mdadm/mkconf
	install -Dm0755 ${S}/debian/checkarray ${D}${datadir}/mdadm/checkarray
	install -Dm0755 ${S}/debian/bugscript ${D}${datadir}/bug/mdadm/script
	install -Dm0644 ${S}/debian/presubj ${D}${datadir}/bug/mdadm/presubj

	install -d ${D}${sysconfdir}/cron.d \
	           ${D}${sysconfdir}/cron.daily \
	           ${D}${sysconfdir}/init.d \
	           ${D}${sysconfdir}/logcheck/ignore.d.server \
	           ${D}${sysconfdir}/logcheck/violations.d

	install -m 0644 ${S}/debian/mdadm.cron.d ${D}${sysconfdir}/cron.d/mdadm
	install -m 0755 ${S}/debian/mdadm.cron.daily ${D}${sysconfdir}/cron.daily/mdadm

	install -m 0755 ${S}/debian/mdadm.init ${D}${sysconfdir}/init.d/mdadm
	install -m 0755 ${S}/debian/mdadm-raid ${D}${sysconfdir}/init.d/
	install -m 0755 ${S}/debian/mdadm-waitidle ${D}${sysconfdir}/init.d/

	install -m 0644 ${S}/debian/mdadm.logcheck.ignore.server ${D}${sysconfdir}/logcheck/ignore.d.server/mdadm
	install -m 0644 ${S}/debian/mdadm.logcheck.violations ${D}${sysconfdir}/logcheck/violations.d/mdadm
}

FILES_${PN} += " \
    ${datadir}/initramfs-tools/* \
    ${datadir}/bug/* \
    ${systemd_unitdir}/* \
"

inherit ptest

do_compile_ptest() {
	oe_runmake test
}

do_install_ptest() {
	cp -a ${S}/tests ${D}${PTEST_PATH}/tests
	cp ${S}/test ${D}${PTEST_PATH}
	sed -e 's!sleep 0.*!sleep 1!g; s!/var/tmp!/!g' -i ${D}${PTEST_PATH}/test
	ln -s /sbin/mdadm ${D}${PTEST_PATH}/mdadm
	for prg in test_stripe swap_super raid6check
	do
		install -D -m 755 $prg ${D}${PTEST_PATH}/
	done
}
RDEPENDS_${PN}-ptest += "bash"
RRECOMMENDS_${PN}-ptest += " \
    coreutils \
    util-linux \
    kernel-module-loop \
    kernel-module-linear \
    kernel-module-raid0 \
    kernel-module-raid1 \
    kernel-module-raid10 \
    kernel-module-raid456 \
"
