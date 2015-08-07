PR = "r0"

inherit debian-package

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://debian/copyright.in;md5=5463e5abae4528d959d7f7a2e91d6176"

INHIBIT_DEFAULT_DEPS = "1"
ROOT_HOME = "/root"

# Follow debian/rules
OSNAME = "GNU/Linux"
do_install() {
	cd ${D} && install -d $(cat ${S}/debian/directory-list)

	install -d ${D}${docdir}/${DPN}
	install -d ${D}${sysconfdir}/dpkg/origins
	install -p -m 644 ${S}/origins/* ${D}${sysconfdir}/dpkg/origins

	install -d ${D}${datadir}/${DPN}
	install -p -m 644 ${S}/debian/changelog ${S}/debian/README \
		${S}/debian/README.FHS ${D}${docdir}
        cat ${S}/debian/copyright.in | sed -e "s&#OSNAME#&${OSNAME}&g" \
                > ${D}${docdir}/copyright
        chmod 644 ${D}${docdir}/copyright

	install -p -m 644 ${S}/share/* ${D}${datadir}/${DPN}
	install -p -m 644 ${S}/licenses/* ${D}${datadir}/common-licenses
	install -p -m 644 ${S}/debian/lintian \
		${D}${datadir}/lintian/overrides/${DPN}
	ln -s GFDL-1.3 ${D}${datadir}/common-licenses/GFDL
	ln -s LGPL-3 ${D}${datadir}/common-licenses/LGPL
	ln -s GPL-3 ${D}${datadir}/common-licenses/GPL
	ln -s README ${D}${docdir}/FAQ
	install -p -m 644 ${S}/etc/* ${D}${sysconfdir}

	sed -e "s&#OSNAME#&${OSNAME}&g" ${S}/share/motd \
		> ${D}${datadir}/${DPN}/motd
	sed -e "s&#OSNAME#&${OSNAME}&g" ${S}/share/info.dir \
		> ${D}${datadir}/${DPN}/info.dir
	sed -e "s&#OSNAME#&${OSNAME}&g" ${S}/etc/issue \
		> ${D}${sysconfdir}/issue
	sed -e "s&#OSNAME#&${OSNAME}&g" ${S}/etc/issue.net \
		> ${D}${sysconfdir}/issue.net
	sed -e "s&#OSNAME#&${OSNAME}&g" ${S}/etc/os-release \
		> ${D}${sysconfdir}/os-release

	mv ${D}${sysconfdir}/os-release ${D}${libdir}
	ln -s ..${libdir}/os-release ${D}${sysconfdir}/os-release
	gzip -9 ${D}${docdir}/changelog

        cd ${D} && chmod 755  $(find . -type d)
        cd ${D} && chmod 1777 $(cat ${S}/debian/1777-dirs)
        cd ${D} && chmod 2775 $(cat ${S}/debian/2775-dirs)
        cd ${D} && chmod 700 root

	# /var/run and /var/lock are checked if they are link or not
	# follow FILESYSTEM_PERMS_TABLES or meta/file/fs-perms.txt, 
	# so temporary remove them to pass do_package.
	# They will be linked in postinst.
	rm -r ${D}${localstatedir}/run
	rm -r ${D}${localstatedir}/lock
}

# Follow debian/postinst.in.
pkg_postinst_${PN}() {
	install_local_dir() {
		if [ ! -d $1 ]; then
			mkdir -p $1
		fi
	}

	install_from_default() {
		if [ ! -f $2 ]; then
			cp -p $1 $2
		fi
	}

	install_directory() {
		if [ ! -d $1 ]; then
			install -d -m $2 $1
		fi
	}

	migrate_directory() {
		if [ ! -L $1 ]; then
			rmdir $1
			ln -s $2 $1
		fi
	}
	if [ ! -e $D${sysconfdir}/dpkg/origins/default ]; then
		if [ -e $D${sysconfdir}/dpkg/origins/debian ]; then
			ln -sf debian $D${sysconfdir}/dpkg/origins/default
		fi
	fi

	install_from_default $D${datadir}/${DPN}/nsswitch.conf	$D${sysconfdir}/nsswitch.conf
	install_from_default $D${datadir}/${DPN}/dot.profile	$D${ROOT_HOME}/.profile
	install_from_default $D${datadir}/${DPN}/dot.bashrc	$D${ROOT_HOME}/.bashrc
	install_from_default $D${datadir}/${DPN}/profile	$D${sysconfdir}/profile
	install_from_default $D${datadir}/${DPN}/motd		$D${sysconfdir}/motd

	install_directory $D/mnt	755
	install_directory $D/srv	755
	install_directory $D/opt	755
	install_directory $D/etc/opt	755
	install_directory $D/var/opt	755
	install_directory $D/media	755

	install_directory $D/var/mail	2775
	if [ ! -L $D${localstatedir}/spool/mail ]; then
		ln -s ../mail $D${localstatedir}/spool/mail
	fi
	install_directory $D/run/lock	1777
	migrate_directory $D${localstatedir}/run	../run
	migrate_directory $D${localstatedir}/lock	../run/lock

	install_local_dir $D${prefix}/local
	install_local_dir $D${prefix}/local/share
	install_local_dir $D${prefix}/local/share/man
	install_local_dir $D${prefix}/local/bin
	install_local_dir $D${prefix}/local/games
	install_local_dir $D${prefix}/local/lib
	install_local_dir $D${prefix}/local/include
	install_local_dir $D${prefix}/local/sbin
	install_local_dir $D${prefix}/local/src
	install_local_dir $D${prefix}/local/etc

	ln -sf share/man $D${prefix}/local/man

	if [ ! -f $D${localstatedir}/log/wtmp ]; then
		echo -n>$D${localstatedir}/log/wtmp
	fi
	if [ ! -f $D${localstatedir}/log/btmp ]; then
		echo -n>$D${localstatedir}/log/btmp
	fi
	if [ ! -f $D${localstatedir}/log/lastlog ]; then
		echo -n>$D${localstatedir}/log/lastlog
	fi
	chmod 664 $D${localstatedir}/log/wtmp $D${localstatedir}/log/lastlog
	chmod 660 $D${localstatedir}/log/btmp
	if [ ! -f $D${localstatedir}/run/utmp ]; then
		echo -n>$D${localstatedir}/run/utmp
	fi
	chmod 664 $D${localstatedir}/run/utmp

	if [ ! -d $D${localstatedir}/lib/dpkg ]; then
		mkdir -m 755 -p $D${localstatedir}/lib/dpkg
	fi
	if [ ! -f $D${localstatedir}/lib/dpkg/status ]; then
		echo > $D${localstatedir}/lib/dpkg/status
		chmod 644 $D${localstatedir}/lib/dpkg/status
	fi

	if [ ! -f $D${prefix}/info/dir ] && [ ! -f $D${infodir}/dir ]; then
		install_from_default $D${datadir}/${DPN}/info.dir $D${infodir}/dir
		chmod 644 $D${infodir}/dir
	fi
}

PACKAGES = "${PN}-doc ${PN} ${PN}-dev ${PN}-dbg"
FILES_${PN} = "/"
FILES_${PN}-doc = "${docdir} ${datadir}/common-licenses"

PACKAGE_ARCH = "${MACHINE_ARCH}"

# Follow base-files conffile from Debian
CONFFILES_${PN} = " \
	${sysconfdir}/debian_version \
	${sysconfdir}/dpkg/origins/debian \
	${sysconfdir}/host.conf \
	${sysconfdir}/issue \
	${sysconfdir}/issue.net \
"
