#
# Base recipe: meta/recipes-core/sysvinit/sysvinit_2.88dsf.bb
# Base branch: daisy
#

require sysvinit.inc

PR = "${INC_PR}.0"

PROVIDES += "initscripts"

PACKAGES =+ "sysv-rc bootlogd bootlogd-doc initscripts initscripts-doc \
             ${PN}-core FILES_${PN}-core-doc ${PN}-utils"
FILES_${PN} += " \
		${base_libdir}/${PN}/*"
FILES_${PN}-dbg += " \
		${base_libdir}/${PN}/.debug"
FILES_sysv-rc += " \
		${sysconfdir}/init.d/README \
		${sysconfdir}/init.d/rc \
		${sysconfdir}/init.d/rc5 \
		${datadir}/sysv-rc"
FILES_bootlogd += " \
		${base_sbindir}/bootlogd \
		${sysconfdir}/init.d/bootlogd \
		${sysconfdir}/init.d/stop*"
FILES_bootlogd-doc += " \
		${mandir}/man8/bootlogd.8"
FILES_initscripts += " \
		${base_bindir}/mountpoint \
		${sysconfdir}/default/* \
		${sysconfdir}/init.d/* \
		${sysconfdir}/network \
		${base_libdir}/init/* \
		run sys \
		${base_sbindir}/fsck.nfs \
		${localstatedir}${base_libdir}/initscripts \
		${localstatedir}${base_libdir}/urandom \
		${localstatedir}/log/fsck"
FILES_initscripts-doc += " \
		${mandir}/man1/mountpoint.1 \
		${mandir}/man5/halt.5 \
		${mandir}/man5/rcS.5 \
		${mandir}/man5/tmpfs.5 \
		${mandir}/man8/fsck.nfs.8"
FILES_${PN}-core += " \
		${base_sbindir}/halt \
		${base_sbindir}/init \
		${base_sbindir}/poweroff \
		${base_sbindir}/reboot \
		${base_sbindir}/runlevel \
		${base_sbindir}/shutdown \
		${base_sbindir}/telinit \
		${includedir}"
FILES_${PN}-core-doc += " \
		${datadir}/man/man5/* \
		${datadir}/man/man8/halt.8 \
		${datadir}/man/man8/init.8 \
		${datadir}/man/man8/poweroff.8 \
		${datadir}/man/man8/reboot.8 \
		${datadir}/man/man8/runlevel.8 \
		${datadir}/man/man8/shutdown.8 \
		${datadir}/man/man8/halt.8"
FILES_${PN}-utils += " \
		${base_bindir}/pidof \
		${base_libdir}/init/init-d-script \
		${base_sbindir}/fstab-decode \
		${base_sbindir}/killall5 \
		${base_sbindir}/sulogin \
		${bindir}/last \
		${bindir}/lastb \
		${bindir}/mesg \
		${sbindir}/service"
FILES_${PN}-utils-doc += " \
		${datardir}/man/man1/last.1 \
		${datardir}/man/man1/lastb.1 \
		${datardir}/man/man1/mesg.1 \
		${datardir}/man/man5/init-d-script.5 \
		${datardir}/man/man8/fstab-decode.8 \
		${datardir}/man/man8/killall5.8 \
		${datardir}/man/man8/pidof.8 \
		${datardir}/man/man8/service.8 \
		${datardir}/man/man8/sulogin.8"

pkg_postinst_bootlogd() {
set -e
if [ -x $D${sysconfdir}/init.d/bootlogd ]; then
	update-rc.d bootlogd             defaults >/dev/null || exit $?
fi
if [ -x $D${sysconfdir}/init.d/stop-bootlogd-single ]; then
	update-rc.d stop-bootlogd-single defaults >/dev/null || exit $?
fi
if [ -x $D${sysconfdir}/stop-bootlogd ]; then
	update-rc.d stop-bootlogd        defaults >/dev/null || exit $?
fi

#
# Create initial log file
#

for F in $D${localstatedir}${base_libdir}/boot
do
	if [ ! -f "$F" ] && touch "$F" >/dev/null 2>&1
	then
		echo "(Nothing has been logged yet.)" >| "$F"
		chown root:adm "$F"
		chmod 640 "$F"
	fi
done
}

pkg_postrm_bootlogd() {
set -e

case "$1" in
    purge)
	#
	# Remove log files
	#
	rm -f $D${localstatedir}/log/boot
	
	# Remove rc symlinks in the reverse dependency order they were
	# inserted
	update-rc.d stop-bootlogd        remove >/dev/null || exit $?
	update-rc.d stop-bootlogd-single remove >/dev/null || exit $?
	update-rc.d bootlogd             remove >/dev/null || exit $?
	;;
esac
}

pkg_postinst_initscripts() {
#
# Links in runlevel S
#
if [ -x $D${sysconfdir}/init.d/mountkernfs.sh ]; then
update-rc.d mountkernfs.sh         defaults >/dev/null || exit $?
fi
if [ -x $D${sysconfdir}/init.d/hostname.sh ]; then
update-rc.d hostname.sh            defaults >/dev/null || exit $?
fi
if [ -x $D${sysconfdir}/init.d/mountdevsubfs.sh ]; then
update-rc.d mountdevsubfs.sh       defaults >/dev/null || exit $?
fi
if [ -x $D${sysconfdir}/init.d/checkroot.sh ]; then
update-rc.d checkroot.sh           defaults >/dev/null || exit $?
fi
if [ -x $D${sysconfdir}/init.d/checkfs.sh ]; then
update-rc.d checkfs.sh             defaults >/dev/null || exit $?
fi
if [ -x $D${sysconfdir}/init.d/mountall.sh ]; then
update-rc.d mountall.sh            defaults >/dev/null || exit $?
fi
if [ -x $D${sysconfdir}init.d/mountall-bootclean.sh ]; then
update-rc.d mountall-bootclean.sh  defaults >/dev/null || exit $?
fi
if [ -x $D${sysconfdir}/init.d/mountnfs.sh ]; then
update-rc.d mountnfs.sh            defaults >/dev/null || exit $?
fi
if [ -x $D${sysconfdir}/init.d/mountnfs-bootclean.sh ]; then
update-rc.d mountall-bootclean.sh  defaults >/dev/null || exit $?
fi
if [ -x $D${sysconfdir}/init.d/mountnfs.sh ]; then
update-rc.d mountnfs.sh            defaults >/dev/null || exit $?
fi
if [ -x $D${sysconfdir}/init.d/mountnfs-bootclean.sh ]; then
update-rc.d mountnfs-bootclean.sh  defaults >/dev/null || exit $?
fi
if [ -x $D${sysconfdir}/init.d/bootmisc.sh ]; then
update-rc.d bootmisc.sh            defaults >/dev/null || exit $?
fi
if [ -x $D${sysconfdir}/init.d/urandom ]; then
update-rc.d urandom                defaults >/dev/null || exit $?
fi
}
