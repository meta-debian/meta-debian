#
# base recipe: meta/recipes-extended/watchdog/watchdog_5.14.bb
# base branch: master
# base commit: 98487ccb5f10b20509c63238b677a3fd4674e9d0
#

SUMMARY = "Software watchdog"
DESCRIPTION = "Watchdog is a daemon that checks if your system is still \
working. If programs in user space are not longer executed \
it will reboot the system."
HOMEPAGE = "http://watchdog.sourceforge.net/"
BUGTRACKER = "http://sourceforge.net/tracker/?group_id=172030&atid=860194"

PR = "r0"
inherit debian-package

#Declare debian pactch type
DEBIAN_PATCH_TYPE = "nopatch"
LICENSE = "GPL-2.0+"
LIC_FILES_CHKSUM = "file://COPYING;md5=ecc0551bf54ad97f6b541720f84d6569"

#fixsepbuild.patch:
#	this patch is correct the path to watchdog-conf file
#watchdog-conf.patch:
#	declare path for watchdog-device	
SRC_URI += "file://fixsepbuild.patch \
	   file://watchdog-conf.patch"

inherit autotools

INITSCRIPT_NAME = "watchdog.sh"
INITSCRIPT_PARAMS = "start 15 1 2 3 4 5 . stop 85 0 6 ."

RRECOMMENDS_${PN} = "kernel-module-softdog"

#ship packages
FILES_${PN} += "${base_libdir}/systemd/system/* ${sysconfdir}/init.d/*"

#install follow Debian jessies
do_install_append() {
	#Create lib/sytemd/system folder
	install -d ${D}${base_libdir}
	install -d ${D}${base_libdir}/systemd
	install -d ${D}${base_libdir}/systemd/system
	chmod 755 ${D}${base_libdir}/systemd/system
	
	#Create /etc/init.d folder
	install -d ${D}${sysconfdir}
	install -d ${D}${sysconfdir}/init.d
	chmod 755 ${D}${sysconfdir}/init.d

	#Install lib/systemd/sytem/watchdog.service and wd_keepalive.service
	install -m 0644 ${S}/debian/watchdog.service ${D}${base_libdir}/systemd/system/
	install -m 0644 ${S}/debian/wd_keepalive.service ${D}${base_libdir}/systemd/system/
	
	#Install /etc/init.d/watchdog and wd_keepalive
	install -m 0644 ${S}/debian/init ${D}${sysconfdir}/init.d/watchdog
	install -m 0644 ${S}/debian/wd_keepalive.init ${D}${sysconfdir}/init.d/wd_keepalive
		 
	install -D ${S}/redhat/watchdog.init ${D}/${sysconfdir}/init.d/watchdog.sh
}
