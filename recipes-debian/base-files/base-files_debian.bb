PR = "r0"

inherit debian-package
PV = "8+deb8u9"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://debian/copyright.in;md5=5463e5abae4528d959d7f7a2e91d6176"

INHIBIT_DEFAULT_DEPS = "1"
ROOT_HOME = "/root"

OSNAME = "GNU/Linux"
do_install() {
	#
	# first half; almost same as debian/rules
	#

	cd ${D} && install -d $(cat ${S}/debian/directory-list)
	# remove unneeded directory
	rm -r ${D}${prefix}/games

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

	#
	# second half; almost same as postinst
	#

	ln -sf debian ${D}${sysconfdir}/dpkg/origins/default

	cp -p ${D}${datadir}/${DPN}/nsswitch.conf ${D}${sysconfdir}
	cp -p ${D}${datadir}/${DPN}/dot.profile   ${D}${ROOT_HOME}/.profile
	cp -p ${D}${datadir}/${DPN}/dot.bashrc    ${D}${ROOT_HOME}/.bashrc
	cp -p ${D}${datadir}/${DPN}/profile       ${D}${sysconfdir}
	cp -p ${D}${datadir}/${DPN}/motd          ${D}${sysconfdir}

	install -d ${D}/mnt
	install -d ${D}/srv
	install -d ${D}/opt
	install -d ${D}${sysconfdir}/opt
	install -d ${D}${localstatedir}/opt
	install -d ${D}/media

	install -d -m 2775 ${D}${localstatedir}/mail
	ln -s ../mail ${D}${localstatedir}/spool/mail
	install -d -m 1777 ${D}/run/lock
	# run and lock are listed in debian/directory-list,
	# so remove them and create symlinks instead
	rm -r ${D}${localstatedir}/run
	rm -r ${D}${localstatedir}/lock
	ln -s ../run ${D}${localstatedir}/run
	ln -s ../run/lock ${D}${localstatedir}/lock

	install -d ${D}${prefix}/local/share/man
	install -d ${D}${prefix}/local/bin
	install -d ${D}${prefix}/local/lib
	install -d ${D}${prefix}/local/include
	install -d ${D}${prefix}/local/sbin
	install -d ${D}${prefix}/local/src
	install -d ${D}${prefix}/local/etc

	ln -sf share/man ${D}${prefix}/local/man

	for f in log/wtmp log/btmp log/lastlog run/utmp; do
		echo -n > ${D}${localstatedir}/${f}
	done
	chmod 664 ${D}${localstatedir}/log/wtmp
	chmod 664 ${D}${localstatedir}/log/lastlog
	chmod 660 ${D}${localstatedir}/log/btmp
	chmod 664 ${D}${localstatedir}/run/utmp

	install -d ${D}${localstatedir}/lib/dpkg
	echo > ${D}${localstatedir}/lib/dpkg/status
	chmod 644 ${D}${localstatedir}/lib/dpkg/status

	cp -p ${D}${datadir}/${DPN}/info.dir ${D}${infodir}/dir
	chmod 644 ${D}${infodir}/dir
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

# move non-essential files to ${PN}-debian
# ${PN} should include only core files for various embedded systems
PACKAGES = "${PN}-debian ${PN}"

FILES_${PN} = "/"
FILES_${PN}-debian = "${sysconfdir}/default \
                      ${sysconfdir}/dpkg \
                      ${sysconfdir}/opt \
                      ${sysconfdir}/os-release \
                      ${includedir} \
                      ${libdir}/os-release \
                      ${prefix}/local \
                      ${datadir}/${DPN}/info.dir \
                      ${datadir}/${DPN}/staff-group-for-usr-local \
                      ${datadir}/common-licenses \
                      ${datadir}/dict \
                      ${docdir} \
                      ${infodir} \
                      ${datadir}/lintian \
                      ${datadir}/man \
                      ${datadir}/misc \
                      ${prefix}/src \
                      ${localstatedir}/backups \
                      ${localstatedir}/lib/misc \
                      ${localstatedir}/mail \
                      ${localstatedir}/opt \
                      ${localstatedir}/spool/mail \
                     "

# Follow base-files conffile from Debian
CONFFILES_${PN} = "${sysconfdir}/debian_version \
                   ${sysconfdir}/host.conf \
                   ${sysconfdir}/issue \
                   ${sysconfdir}/issue.net \
                  "
CONFFILES_${PN}-debian = "${sysconfdir}/dpkg/origins/debian"
