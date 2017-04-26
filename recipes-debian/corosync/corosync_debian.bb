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

PR = "r2"

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

PACKAGES =+ "libcfg libcfg-dev libconfdb libconfdb-dev \
             libcoroipcc libcoroipcc-dev libcoroipcs libcoroipcs-dev \
             libcpg libcpg-dev libevs libevs-dev liblogsys liblogsys-dev \
             libpload libpload-dev libquorum libquorum-dev \
             libsam libsam-dev libtotem-pg libtotem-pg-dev \
             libvotequorum libvotequorum-dev"

FILES_libcfg = "${libdir}/libcfg${SOLIBS}"
FILES_libcfg-dev = "${includedir}/${PN}/cfg.h \
                    ${libdir}/libcfg.so \
                    ${libdir}/pkgconfig/libcfg.pc"
RDEPENDS_libcfg-dev = "libcfg"
FILES_libcfg-staticdev = "${libdir}/libcfg.a"
FILES_libconfdb = "${libdir}/libconfdb${SOLIBS}"
FILES_libconfdb-dev = "${includedir}/${PN}/confdb.h \
                       ${libdir}/libconfdb.so \
                       ${libdir}/pkgconfig/libconfdb.pc"
RDEPENDS_libconfdb-dev = "libconfdb"
FILES_libconfdb-staticdev = "${libdir}/libconfdb.a"
FILES_libcoroipcc = "${libdir}/libcoroipcc${SOLIBS}"
FILES_libcoroipcc-dev = "${includedir}/${PN}/coroipcc.h \
                         ${libdir}/libcoroipcc.so \
                         ${libdir}/pkgconfig/libcoroipcc.pc"
RDEPENDS_libcoroipcc-dev = "libcoroipcc"
FILES_libcoroipcc-staticdev = "${libdir}/libcoroipcc.a"
FILES_libcoroipcs = "${libdir}/libcoroipcs${SOLIBS}"
FILES_libcoroipcs-dev = "${includedir}/${PN}/coroipcs.h \
                         ${libdir}/libcoroipcs.so \
                         ${libdir}/pkgconfig/libcoroipcs.pc"
RDEPENDS_libcoroipcs-dev = "libcoroipcs"
FILES_libcoroipcs-staticdev = "${libdir}/libcoroipcs.a"
FILES_libcpg = "${libdir}/libcpg${SOLIBS}"
FILES_libcpg-dev = "${includedir}/${PN}/cpg.h \
                    ${libdir}/libcpg.so \
                    ${libdir}/pkgconfig/libcpg.pc"
RDEPENDS_libcpg-dev = "libcpg"
FILES_libcpg-staticdev = "${libdir}/libcpg.a"
FILES_libevs = "${libdir}/libevs${SOLIBS}"
FILES_libevs-dev = "${includedir}/${PN}/evs.h \
                    ${libdir}/libevs.so \
                    ${libdir}/pkgconfig/libevs.pc"
RDEPENDS_libevs-dev = "libevs"
FILES_libevs-staticdev = "${libdir}/libevs.a"
FILES_liblogsys = "${libdir}/liblogsys${SOLIBS}"
FILES_liblogsys-dev = "${includedir}/${PN}/engine/logsys.h \
                       ${libdir}/liblogsys.so \
                       ${libdir}/pkgconfig/liblogsys.pc"
RDEPENDS_liblogsys-dev = "liblogsys"
FILES_liblogsys-staticdev = "${libdir}/liblogsys.a"
FILES_libpload = "${libdir}/libpload${SOLIBS}"
FILES_libpload-dev = "${libdir}/libpload.so \
                      ${libdir}/pkgconfig/libpload.pc"
RDEPENDS_libpload-dev = "libpload"
FILES_libpload-staticdev = "${libdir}/libpload.a"
FILES_libquorum = "${libdir}/libquorum${SOLIBS}"
FILES_libquorum-dev = "${includedir}/${PN}/quorum.h \
                       ${libdir}/libquorum.so \
                       ${libdir}/pkgconfig/libquorum.pc"
RDEPENDS_libquorum-dev = "libquorum"
FILES_libquorum-staticdev = "${libdir}/libquorum.a"
FILES_libsam = "${libdir}/libsam${SOLIBS}"
FILES_libsam-dev = "${includedir}/${PN}/sam.h \
                    ${libdir}/libsam.so \
                    ${libdir}/pkgconfig/libsam.pc"
RDEPENDS_libsam-dev = "libsam"
FILES_libsam-staticdev = "${libdir}/libsam.a"
FILES_libtotem-pg = "${libdir}/libtotem_pg${SOLIBS}"
FILES_libtotem-pg-dev = "${includedir}/${PN}/totem/coropoll.h \
                         ${includedir}/${PN}/totem/totem.h \
                         ${includedir}/${PN}/totem/totemip.h \
                         ${includedir}/${PN}/totem/totempg.h \
                         ${libdir}/libtotem_pg.so \
                         ${libdir}/pkgconfig/libtotem_pg.pc"
RDEPENDS_libtotem-pg-dev = "libtotem-pg"
FILES_libtotem-pg-staticdev = "${libdir}/libtotem_pg.a"
FILES_libvotequorum = "${libdir}/libvotequorum${SOLIBS}"
FILES_libvotequorum-dev = "${includedir}/${PN}/votequorum.h \
                           ${libdir}/libvotequorum.so \
                           ${libdir}/pkgconfig/libvotequorum.pc"
RDEPENDS_libvotequorum-dev = "libvotequorum"
FILES_libvotequorum-staticdev = "${libdir}/libvotequorum.a"
FILES_${PN} += "/run ${libdir}/lcrso/*"
FILES_${PN}-dbg += "${libdir}/lcrso/.debug"

# Dependency between packages follow debian/control
RDEPENDS_${PN} += "libcfg libconfdb libcoroipcc libcoroipcs \
                   libcpg libevs liblogsys libpload \
                   libquorum libsam libtotem-pg libvotequorum \
                   "
