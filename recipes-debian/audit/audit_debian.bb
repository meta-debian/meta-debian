#
# base recipe http://git.yoctoproject.org/cgit/cgit.cgi/meta-selinux/tree/
# recipes-security/audit/audit_2.4.3.bb?h=master
# base branch: master
#

inherit debian-package
PV = "2.4"

PR = "r2"

LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
	file://lib/libaudit.h;beginline=1;endline=22;md5=26d304bf07003c3bbdabf21836cfd3c1"

SRC_URI += " \
	file://audit-python.patch \
	file://audit-python-configure.patch \
	file://cross-compile_debian.patch \
	file://fix-swig-host-contamination.patch \
"
inherit autotools-brokensep pythonnative systemd

DEPENDS += " \
	python libldap libcap-ng tcp-wrappers \
	linux-libc-headers (>= 2.6.30) libprelude ${PN}-native"

export BUILD_SYS
export HOST_SYS
export STAGING_INCDIR
export STAGING_LIBDIR

PARALLEL_MAKE = ""
EXTRA_OECONF += "--with-prelude \
	--with-libwrap \
	--with-apparmor \
	--enable-gssapi-krb5=no \
	--with-libcap-ng=yes \
	--with-python \
	--libdir=${base_libdir} \
	--sbindir=${base_sbindir} \
	--without-golang \
	"
EXTRA_OECONF_append_arm = " --with-arm=yes"

EXTRA_OEMAKE += "PYLIBVER='python${PYTHON_BASEVERSION}' \
	PYINC='${STAGING_INCDIR}/$(PYLIBVER)' \
	pyexecdir=${libdir}/python${PYTHON_BASEVERSION}/dist-packages \
	STDINC='${STAGING_INCDIR}' \
	"
SYSTEMD_SERVICE_${PN}d = "auditd.service"

do_configure_prepend () {
	sed -i "s:(STAGING_INCDIR):\${STAGING_INCDIR}:" ${S}/swig/Makefile.am
	sed -i "s:(STAGING_INCDIR):\${STAGING_INCDIR}:g" ${S}/swig/auditswig.i
}

do_compile_append() {
	oe_runmake -C ${S}/bindings/python \
		PYTHON_INCLUDES="-I${STAGING_INCDIR}/python${PYTHON_BASEVERSION}"
}
do_install_append() {
	rm -f ${D}${libdir}/python${PYTHON_BASEVERSION}/dist-packages/*.a
	rm -f ${D}${libdir}/python${PYTHON_BASEVERSION}/dist-packages/*.la
	rm -f ${D}${libdir}/python${PYTHON_BASEVERSION}/dist-packages/audit.pyo
	rm -r ${D}${base_libdir}/pkgconfig
	rm -f ${D}${sysconfdir}/audit/audit.rules

	install -d ${D}${sysconfdir}/default
	install -d ${D}${sysconfdir}/init.d
	mv ${D}${sysconfdir}/sysconfig/auditd ${D}${sysconfdir}/default
	rmdir ${D}${sysconfdir}/sysconfig/

	# replace init.d
	install -m 0755 ${S}/debian/auditd.init ${D}${sysconfdir}/init.d/auditd
	rm -rf ${D}${sysconfdir}/rc.d
	
	# install systemd unit files
	install -D -m 0644 ${S}/init.d/auditd.service ${D}${systemd_system_unitdir}/auditd.service

	chmod 750 ${D}${sysconfdir}/audit ${D}${sysconfdir}/audit/rules.d
	chmod 640 ${D}${sysconfdir}/audit/auditd.conf \
		${D}${sysconfdir}/audit/rules.d/audit.rules

	LINKLIB=$(basename $(readlink ${D}${base_libdir}/libaudit.so))
	rm ${D}${base_libdir}/libaudit.so
	ln -s ../..${base_libdir}/$LINKLIB ${D}${libdir}/libaudit.so

	LINKLIB=$(basename $(readlink ${D}${base_libdir}/libauparse.so))
        rm ${D}${base_libdir}/libauparse.so
        ln -s ../..${base_libdir}/$LINKLIB ${D}${libdir}/libauparse.so
	mv ${D}${base_libdir}/*.a ${D}${libdir}/
	rm ${D}${base_libdir}/*.la

	oe_runmake -C ${S}/bindings/python install DESTDIR=${D}
	rm ${D}${libdir}/*/dist-packages/auparse.a

	# Follow debian/auditd.dirs
	install -d ${D}${localstatedir}/log/audit
}

# Follow debian/auditd.postinst
pkg_postinst_${PN}() {
	if [ ! -f $D${sysconfdir}/audit/audit.rules ]; then
		cp -a $D${sysconfdir}/audit/rules.d/audit.rules \
		      $D${sysconfdir}/audit/audit.rules
	fi
}

PACKAGES =+ "\
	audispd-plugins lib${PN}-common lib${PN} \
	libauparse-dev libauparse python-${PN}"

FILES_audispd-plugins = "\
	${sysconfdir}/audisp/audisp-* ${sysconfdir}/audisp/zos-remote.conf \
	${base_sbindir}/audisp-* ${base_sbindir}/audispd-zos-remote\
	${sysconfdir}/audisp/plugins.d/au-* \
	${sysconfdir}/audisp/plugins.d/audispd-zos-remote.conf"
FILES_lib${PN}-common = "${sysconfdir}/libaudit.conf"
FILES_lib${PN} = "${base_libdir}/libaudit.so.*"
FILES_libauparse-dev = "${includedir}/auparse* ${libdir}/libauparse.so"
FILES_libauparse = "${base_libdir}/libauparse.so.*"
FILES_python-${PN} = "${libdir}/python*/dist-packages/*"
FILES_${PN}-dbg += "${libdir}/python*/dist-packages/.debug"
FILES_${PN} += "${systemd_unitdir}"

DEBIANNAME_${PN} = "${PN}d"
DEBIANNAME_${PN}-dev = "lib${PN}-dev"

#follow debian/control
RDEPENDS_${PN} += "lsb-base"
RDEPENDS_libauparse-dev += "libauparse"
RDEPENDS_lib${PN} += "lib${PN}-common"
RDEPENDS_${PN}-dev += "lib${PN}"
RDEPENDS_audispd-plugins += "${PN}"
