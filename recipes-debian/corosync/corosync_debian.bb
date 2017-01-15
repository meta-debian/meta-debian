SUMMARY = "Standards-based cluster framework"
DESCRIPTION = "The corosync project is a project to implement a production quality\n\
"Revised BSD" licensed implementation of all core functionalities\n\
required by openais. The project implements cutting edge research\n\
on virtual synchrony to provide 100% correct operation in the face of\n\
failures or partitionable networks with excellent performance\n\
characteristics.\n\
.\n\
The Application Interface Specification is a software API and policies\n\
which are used to develop applications that maintain service during\n\
faults."

PR = "r1"

inherit debian-package
PV = "1.4.6"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://LICENSE;md5=25656171d1e4054c636a9893067f8c30"

DEPENDS = "groff-native nss"

inherit autotools pkgconfig useradd

# Follow debian/corosync.postinst
USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} = "--user-group --system --no-create-home ais"

# Follow debian/rules
EXTRA_OECONF = " \
    --libexecdir=${libdir} \
    --with-socket-dir=${localstatedir}/run/${DPN} \
"

do_configure_prepend() {
	# replace AC_REPLACE_FNMATCH by AC_FUNC_FNMATCH,
	# because it causes linking error (fnmatch.h => fnmatch_.h)
	sed -i -e "s@AC_REPLACE_FNMATCH@AC_FUNC_FNMATCH@g" ${S}/configure.ac
}

do_install_append() {
	install -d ${D}${sysconfdir}/corosync \
	           ${D}${sysconfdir}/init.d \
	           ${D}${sysconfdir}/default \
	           ${D}${sysconfdir}/logrotate.d \
	           ${D}${localstatedir}/log/${DPN}

	install -m 0755 ${S}/debian/corosync.init \
	                ${D}${sysconfdir}/init.d/corosync
	install -m 0644 ${S}/debian/corosync.default \
	                ${D}${sysconfdir}/default/corosync
	install -m 0644 ${S}/debian/corosync.logrotate \
	                ${D}${sysconfdir}/logrotate.d/corosync
	install -m 0755 ${S}/debian/corosync.corosync-notifyd.init \
	                ${D}${sysconfdir}/init.d/corosync-notifyd

	cp -ax ${S}/debian/corosync.example-config \
	       ${D}${sysconfdir}/corosync/corosync.conf
	chmod 644 ${D}${libdir}/lcrso/*.lcrso
}

pkg_postinst_${PN}() {
	# Touch an empty file so the log dir stays around after package removal
	# so logrotate doesn't choke
	touch $D${localstatedir}/log/corosync/.empty
}

RDEPENDS_${PN} += "bash lsb-base"

PACKAGES =+ "libcfg libconfdb libcoroipcc libcoroipcs \
             libcpg libevs liblogsys libpload \
             libquorum libsam libtotem-pg libvotequorum \
             "

FILES_libcfg = "${libdir}/libcfg${SOLIBS}"
FILES_libconfdb = "${libdir}/libconfdb${SOLIBS}"
FILES_libcoroipcc = "${libdir}/libcoroipcc${SOLIBS}"
FILES_libcoroipcs = "${libdir}/libcoroipcs${SOLIBS}"
FILES_libcpg = "${libdir}/libcpg${SOLIBS}"
FILES_libevs = "${libdir}/libevs${SOLIBS}"
FILES_liblogsys = "${libdir}/liblogsys${SOLIBS}"
FILES_libpload = "${libdir}/libpload${SOLIBS}"
FILES_libquorum = "${libdir}/libquorum${SOLIBS}"
FILES_libsam = "${libdir}/libsam${SOLIBS}"
FILES_libtotem-pg = "${libdir}/libtotem_pg${SOLIBS}"
FILES_libvotequorum = "${libdir}/libvotequorum${SOLIBS}"
FILES_${PN} += "/run ${libdir}/lcrso/*"
FILES_${PN}-dbg += "${libdir}/lcrso/.debug"

# Dependency between packages follow debian/control
RDEPENDS_${PN} += "libcfg libconfdb libcoroipcc libcoroipcs \
                   libcpg libevs liblogsys libpload \
                   libquorum libsam libtotem-pg libvotequorum \
                   "
