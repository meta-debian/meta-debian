#
# base recipe: meta/recipes-devtools/apt/apt_1.0.10.1.bb
# base branch: jethro
#
require apt.inc

# Skip build test because it depend on gtest package,
# and gtest haven't been available in meta-debian,yet.
SRC_URI += " \
	file://gtest-skip-fix.patch \
"
DEPENDS += "curl db dpkg"

do_configure_prepend() {
	rm -rf ${S}/buildlib/config.sub
	rm -rf ${S}/buildlib/config.guess
}
USE_NLS = "yes"
EXTRA_OECONF += " --with-cpus=1 --with-procs=1 --with-proc-multiply=1"

do_install() {
	install -d ${D}${bindir} ${D}${includedir} ${D}${datadir}
	install -d ${D}${libdir}/${DPN}
	install -d ${D}${libdir}/dpkg/methods/${DPN}
	bin_file="apt apt-cache apt-cdrom apt-config apt-extracttemplates \
		apt-ftparchive apt-get apt-key apt-mark apt-sortpkgs"
	for file in $bin_file; do
		install -m 0755 ${B}/bin/$file ${D}${bindir}
	done
	cp -r ${B}/bin/methods ${D}${libdir}/${DPN}
	install -m 0755 ${B}/bin/apt-helper ${D}${libdir}/${DPN}
	cp ${B}/scripts/dselect/* ${D}${libdir}/dpkg/methods/${DPN}
	cp -R ${B}/bin/*.so* ${D}${libdir}/
	cp -r ${B}/include/apt-pkg ${D}${includedir}
	install -D -m 0644 ${S}/debian/apt.conf.autoremove \
		${D}${sysconfdir}/${DPN}/apt.conf.d/01autoremove
	install -D -m 0755 ${S}/debian/apt.auto-removal.sh \
		${D}${sysconfdir}/kernel/postinst.d/apt-auto-removal
	install -D -m 0755 ${S}/debian/apt.cron.daily \
		${D}${sysconfdir}/cron.daily/apt
	install -D -m 0644 ${S}/debian/apt.logrotate \
		${D}${sysconfdir}/logrotate.d/apt
	install -D -m 0755 ${B}/bin/apt-dump-solver \
		${D}${libdir}/${DPN}/solvers/dump
	install -D -m 0755 ${B}/bin/apt-internal-solver \
		${D}${libdir}/${DPN}/solvers/apt
	install -D -m 0755 ${S}/debian/apt.bug-script \
		${D}${datadir}/bug/apt/script
	cp -r ${B}/locale ${D}${datadir}

	install -D -m 0644 ${S}/vendor/debian/sources.list \
		${D}${docdir}/${DPN}/examples/sources.list
	#follow debian/apt.dirs
	install -d ${D}${libdir}/${DPN}/methods/apt
	install -d ${D}${sysconfdir}/${DPN}/preferences.d
	install -d ${D}${sysconfdir}/${DPN}/sources.list.d
	install -d ${D}${sysconfdir}/${DPN}/trusted.gpg.d
	install -d ${D}${localstatedir}/cache/${DPN}/archives/partial
	install -d ${D}${localstatedir}/lib/${DPN}/lists/partial
	install -d ${D}${localstatedir}/lib/${DPN}/mirrors/partial
	install -d ${D}${localstatedir}/lib/${DPN}/periodic
	install -d ${D}${localstatedir}/log/${DPN}

	#Write the correct apt-architecture to apt.conf
	APT_CONF=${D}${sysconfdir}/apt/apt.conf
	echo 'APT::Architecture "${DPKG_ARCH}";' > ${APT_CONF}
}
# base on debian/postinst
pkg_postinst_${PN} () {
	set -e

	create_apt_conf ()
	{
		EXAMPLE_SOURCE=$D${docdir}/${DPN}/examples/sources.list
		if [ -f $EXAMPLE_SOURCE ]; then
			cp $EXAMPLE_SOURCE $D${sysconfdir}/${DPN}/sources.list
		fi
	}
	#
	# If there is no /etc/apt/sources.list then create a default
	#
	if [ ! -f $D${sysconfdir}/${DPN}/sources.list ]; then
		create_apt_conf
	fi
}
PACKAGES =+ "${DPN}-transport-https ${DPN}-utils lib${DPN}-inst libapt-pkg"
DEBIANNAME_${PN}-dev = "lib${DPN}-pkg-dev"

FILES_${DPN}-transport-https = "${libdir}/${DPN}/methods/https"
FILES_${DPN}-utils = "\
	${bindir}/apt-extracttemplates ${bindir}/apt-ftparchive \
	${bindir}/apt-sortpkgs ${libdir}/${DPN}/solvers/* \
	${datadir}/locale/*/*/apt-utils*"
FILES_lib${DPN}-inst = "\
	${libdir}/libapt-inst.so.* ${datadir}/locale/*/*/libapt-inst*"
FILES_libapt-pkg = "\
	${libdir}/libapt-pkg.so.* ${datadir}/locale/*/*/libapt-pkg*"
FILES_${PN} += "\
	${datadir}/bug/${DPN}/script ${libdir}/dpkg/methods/* ${datadir}/locale"
FILES_${PN}-dbg += "${libdir}/${DPN}/solvers/.debug/*"
