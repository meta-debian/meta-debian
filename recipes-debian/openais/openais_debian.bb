SUMMARY = "Standards-based cluster framework (daemon and modules)"
DESCRIPTION = "\
	The openais project is a project to implement a production quality   \
	'Revised BSD' licensed implementation of the SA Forum's Application   \ 
	Interface Specification. The project implements cutting edge research \
	on virtual synchrony to provide 100% correct operation in the face of \
	failures or partitionable networks with excellent performance \
	characteristics. \
"
PR = "r1"
inherit debian-package
PV = "1.1.4"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "\
	file://LICENSE;md5=4cb00dd52a063edbece6ae248a2ba663"
inherit autotools-brokensep pkgconfig
DEPENDS += "corosync groff"

EXTRA_OECONF += "--with-lcrso-dir=${libdir}/lcrso"
#Empty DEBIAN_QUILT_PATCHES to avoid error :debian/patches not found
DEBIAN_QUILT_PATCHES = ""

# replace AC_REPLACE_FNMATCH by AC_FUNC_FNMATCH not to use AC_LIBOBJ
# because it causes an link error (fnmatch.h => fnmatch_.h)
do_unpack_append() {
    bb.build.exec_func('fix_fnmatch', d)
}
fix_fnmatch() {
	sed -i "s@AC_REPLACE_FNMATCH@AC_FUNC_FNMATCH@g" ${S}/configure.ac
}

#install follow debian jessie
do_install_append() {
	install -D -m 644 ${S}/debian/ckpt-service \
		${D}${sysconfdir}/corosync/service.d/ckpt-service
	rm ${D}${sysconfdir}/corosync/amf.conf.example \
		${D}${sysconfdir}/init.d/openais
}
PACKAGES =+ "\
	libsaamf libsaamf-dev libsackpt libsackpt-dev libsaclm \
	libsaclm-dev libsaevt libsaevt-dev libsalck libsalck-dev \
	libsamsg libsamsg-dev libsatmr libsatmr-dev"

FILES_libsaamf = "${libdir}/libSaAmf.so.*"
FILES_libsaamf-dev = "\
	${includedir}/${DPN}/saAmf.h ${libdir}/libSaAmf.so \
	${libdir}/pkgconfig/libSaAmf.pc"
FILES_libsackpt = "${libdir}/libSaCkpt.so.*"
FILES_libsackpt-dev = "\
	${includedir}/${DPN}/saCkpt.h ${libdir}/libSaCkpt.so \
	${libdir}/pkgconfig/libSaCkpt.pc"
FILES_libsaclm = "${libdir}/libSaClm.so.*"
FILES_libsaclm-dev = "\
	${includedir}/${DPN}/saClm.h ${libdir}/libSaClm.so \
	${libdir}/pkgconfig/libSaClm.pc"
FILES_libsaevt = "${libdir}/libSaEvt.so.*"
FILES_libsaevt-dev = "\
	${includedir}/${DPN}/saEvt.h ${libdir}/libSaEvt.so \
	${libdir}/pkgconfig/libSaEvt.pc"
FILES_libsalck = "${libdir}/libSaLck.so.*"
FILES_libsalck-dev = "\
	${includedir}/${DPN}/saLck.h ${libdir}/libSaLck.so \
	${libdir}/pkgconfig/libSaLck.pc"
FILES_libsamsg = "${libdir}/libSaMsg.so.*"
FILES_libsamsg-dev = "\
	${includedir}/${DPN}/saMsg.h ${libdir}/libSaMsg.so \
	${libdir}/pkgconfig/libSaMsg.pc"
FILES_libsatmr = "${libdir}/libSaTmr.so.*"
FILES_libsatmr-dev = "\
	${includedir}/${DPN}/saTmr.h ${libdir}/libSaTmr.so \
	${libdir}/pkgconfig/libSaTmr.pc"
FILES_${PN} += "${libdir}/lcrso/*"
FILES_${PN}-dbg += "${libdir}/lcrso/.debug/*"

#Correct the packages name:
DEBIANNAME_libsaamf-dev = "libsaamf3-dev"
DEBIANNAME_libsackpt-dev = "libsackpt3-dev"
DEBIANNAME_libsaclm-dev = "libsaclm3-dev"
DEBIANNAME_libsaevt-dev = " libsaevt3-dev"
DEBIANNAME_libsalck-dev = "libsalck3-dev"
DEBIANNAME_libsamsg-dev = "libsamsg4-dev"
DEBIANNAME_libsatmr-dev = "libsatmr3-dev"

#follow debian/control
RDEPENDS_${PN} += "corosync libsaamf libsackpt libsaclm libsaevt libsalck \
	libsamsg libsatmr"
RDEPENDS_${PN}-dev += "libsaamf-dev libsackpt-dev libsaclm-dev libsaevt-dev \
	libsalck-dev libsamsg-dev libsatmr-dev"
RDEPENDS_libsaamf-dev += "libsaamf"
RDEPENDS_libsackpt-dev += "libsackpt"
RDEPENDS_libsaclm-dev += "libsaclm"
DEPENDS_libsaevt-dev += "libsaevt"
RDEPENDS_libsalck-dev += "libsalck"
RDEPENDS_libsamsg-dev += "libsamsg"
RDEPENDS_libsatmr-dev += "libsatmr"
