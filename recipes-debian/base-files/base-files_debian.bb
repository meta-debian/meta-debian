SUMMARY = "Miscellaneous files for the base system"
DESCRIPTION = "The base-files package creates the basic system directory \
structure and provides a small set of key configuration files \
for the system."

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://licenses/GPL-2;md5=b234ee4d69f5fce4486a80fdaf4a4263"

inherit debian-package
require recipes-debian/sources/base-files.inc

FILESPATH_append = ":${COREBASE}/meta/recipes-core/base-files/base-files"
SRC_URI += "file://shells"

INHIBIT_DEFAULT_DEPS = "1"

OSNAME = "GNU/Linux"

do_install() {
	( test -f ${S}/debian/base-files.dirs && \
	    cd ${D} && install -d $(cat ${S}/debian/base-files.dirs))

	rm -rf ${D}${localstatedir}/run ${D}${localstatedir}/lock
	ln -sf /run ${D}${localstatedir}/run
	ln -sf /run/lock ${D}${localstatedir}/lock

	install -p -m 644 ${S}/etc/*      ${D}${sysconfdir}/
	install -p -m 755 ${S}/motd/*     ${D}${sysconfdir}/update-motd.d/
	install -p -m 644 ${S}/licenses/* ${D}${datadir}/common-licenses/
	install -p -m 644 ${S}/origins/*  ${D}${sysconfdir}/dpkg/origins/
	install -p -m 644 ${S}/share/*    ${D}${datadir}/base-files/

	sed -e "s&#OSNAME#&${OSNAME}&g" ${S}/share/motd     > ${D}${datadir}/base-files/motd
	sed -e "s&#OSNAME#&${OSNAME}&g" ${S}/share/info.dir > ${D}${datadir}/base-files/info.dir
	sed -e "s&#OSNAME#&${OSNAME}&g" ${S}/etc/issue      > ${D}${sysconfdir}/issue
	sed -e "s&#OSNAME#&${OSNAME}&g" ${S}/etc/issue.net  > ${D}${sysconfdir}/issue.net
	sed -e "s&#OSNAME#&${OSNAME}&g" ${S}/etc/os-release > ${D}${libdir}/os-release
	rm -f ${D}${sysconfdir}/os-release
	ln -sf ..${libdir}/os-release ${D}${sysconfdir}/os-release

	cd ${D}
	#chown root:staff /var/local
	chmod 755  `find ${D} -type d`
	chmod 1777 `cat ${S}/debian/1777-dirs`
	chmod 2775 `cat ${S}/debian/2775-dirs`
	chmod 700 root
	install -d -m 0755 mnt srv opt etc/opt var/opt media
	install -d -m 2755 var/mail
	test -L var/spool/mail || ln -sf ../mail var/spool/mail
	cd -

	install -d ${D}${docdir}/${BPN}/
	install -m 644 ${S}/debian/README ${S}/debian/README.FHS ${D}${docdir}/${BPN}/
	ln -sf README ${D}${docdir}/${BPN}/FAQ
        ln -sf GFDL-1.3 ${D}${datadir}/common-licenses/GFDL
        ln -sf LGPL-3 ${D}${datadir}/common-licenses/LGPL
        ln -sf GPL-3 ${D}${datadir}/common-licenses/GPL

	cp -p ${D}${datadir}/base-files/dot.profile ${D}/root/.profile
	cp -p ${D}${datadir}/base-files/dot.bashrc ${D}/root/.bashrc
	cp -p ${D}${datadir}/base-files/profile ${D}${sysconfdir}/profile
	cp -p ${D}${datadir}/base-files/motd ${D}${sysconfdir}/motd

	# Install local dir
	LOCAL_DIR="share/man bin games lib include sbin src etc"
	for i in $LOCAL_DIR; do
		install -d ${D}${prefix}/local/$i
	done
	ln -sf share/man ${D}${prefix}/local/man

	install -m 0644 ${WORKDIR}/shells ${D}${sysconfdir}/
}

pkg_postinst_ontarget_${PN} () {
	for i in log/wtmp log/btmp log/lastlog run/utmp; do
		test -f $D${localstatedir}/$i || echo -n > $D${localstatedir}/$i
		chown root:utmp $D${localstatedir}/$i
		chmod 664 $D${localstatedir}/$i
	done
	chmod 660 $D${localstatedir}/log/btmp

	test -d $D${localstatedir}/lib/dpkg || install -d $D${localstatedir}/lib/dpkg
	test -f $D${localstatedir}/lib/dpkg/status || echo > $D${localstatedir}/lib/dpkg/status
	chmod 644 $D${localstatedir}/lib/dpkg/status

	if [ ! -f $D${prefix}/info/dir ] && [ ! -f $D${infodir}/dir ]; then
		cp -p $D${datadir}/base-files/info.dir $D${infodir}/dir
		chmod 644 $D${infodir}/dir
	fi
}

SYSROOT_DIRS += "${sysconfdir}/skel"

PACKAGES = "${PN}-doc ${PN} ${PN}-dev ${PN}-dbg"
FILES_${PN} = "/"
FILES_${PN}-doc = "${docdir} ${datadir}/common-licenses"

PACKAGE_ARCH = "${MACHINE_ARCH}"

CONFFILES_${PN} = " \
    ${sysconfdir}/motd \
    ${sysconfdir}/profile"
