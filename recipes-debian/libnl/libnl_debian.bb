# base recipe: meta/recipes-support/libnl/libnl_3.2.25.bb
# base branch: master

SUMMARY = "A library for applications dealing with netlink sockets"
HOMEPAGE = "http://www.infradead.org/~tgr/libnl/"
SECTION = "libs/network"

PR = "r0"
DPN = "libnl3"
inherit debian-package
PV = "3.2.24"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=4fbd65380cdd255951079008b364516c"

DEPENDS = "flex-native bison-native"

inherit autotools pkgconfig

PACKAGES =+ "libnl-3-200 libnl-3-dev libnl-cli-3-200 libnl-utils libnl-route-3-dev libnl-route-3-200 \
libnl-nf-3-dev libnl-nf-3-200 libnl-idiag-3-dev libnl-idiag-3-200 libnl-genl-3-dev"

do_install_append() {
	# install for libnl-3-200 package
	install -d ${D}${base_libdir}/
	mv ${D}${libdir}/libnl-3.so.200* ${D}${base_libdir}/

	# install for libnl-3-dev package
	mv ${D}${libdir}/libnl-3.so ${D}${base_libdir}/
	mv ${D}${libdir}/libnl-3.a ${D}${base_libdir}/

	# install for libnl-genl-3-dev package
	mv ${D}${libdir}/libnl-genl-3.a ${D}${base_libdir}/
	# Relink library
	libname=`readlink ${D}${libdir}/libnl-genl-3.so | xargs basename`
	ln -sf ..${libdir}/${libname} ${D}${base_libdir}/libnl-genl-3.so
	rm -rf ${D}${libdir}/libnl-genl-3.so

	#correct the path to libnl-3.so* file
	sed -i -e "s:libdir='${libdir}':libdir='${base_libdir}':" ${D}${libdir}/libnl-3.la
}

FILES_libnl-3-200 = " \
	${base_libdir}/libnl-3.so.200 \
	${base_libdir}/libnl-3.so.200.19.0 \
	${sysconfdir}/libnl-3/classid \
	${sysconfdir}/libnl-3/pktloc \
    "

FILES_libnl-3-dev = " \
	${base_libdir}/libnl-3.so \
	${includedir}/libnl3/netlink/* \
	${libdir}/pkgconfig/libnl-3.0.pc \
    "

FILES_libnl-cli-3-200 = " \
	${libdir}/libnl/cli/*/*.so \
	${libdir}/libnl-cli-3.so.200* \
    "

FILES_libnl-utils = "${sbindir}/*"

FILES_libnl-route-3-dev = " \
	${libdir}/libnl-route-3.so \
	${libdir}/pkgconfig/libnl-route-3.0.pc \
    "

FILES_libnl-route-3-200 = " \
	${libdir}/libnl-route-3.so.200 \
	${libdir}/libnl-route-3.so.200.19.0 \
    "

FILES_libnl-nf-3-dev = " \
	${libdir}/libnl-nf-3.so \
	${libdir}/pkgconfig/libnl-nf-3.0.pc \
    "

FILES_libnl-nf-3-200 = " ${libdir}/libnl-nf-3.so.200*"

FILES_libnl-idiag-3-dev = " \
	${libdir}/libnl-idiag-3.so \
	${libdir}/pkgconfig/libnl-idiag-3.0.pc \
    "

FILES_libnl-idiag-3-200 = " \
	${libdir}/libnl-idiag-3.so.200 \
	${libdir}/libnl-idiag-3.so.200.19.0 \
    "

FILES_libnl-genl-3-dev = " \
	${base_libdir}/libnl-genl-3.so \
	${libdir}/pkgconfig/libnl-genl-3.0.pc \
    "

FILES_libnl-cli-3-dev = " \
	${libdir}/libnl-cli-3.so \
	${libdir}/pkgconfig/libnl-cli-3.0.pc \
    "

FILES_${PN} = " \
	${libdir}/libnl-3.so.* \
	${libdir}/libnl.so.* \
	${sysconfdir} \
    "

RREPLACES_${PN} = "libnl2"
RCONFLICTS_${PN} = "libnl2"

FILES_${PN}-dbg += "${libdir}/libnl/cli/*/.debug"

FILES_${PN}-dev += " \
	${libdir}/libnl/cli/*/*.so \
	${libdir}/libnl/cli/*/*.la \
    "

FILES_${PN}-staticdev += " \
	${libdir}/libnl/cli/*/*.a \
	${libdir}/libnl-route-3.a \
	${base_libdir}/libnl-3.a \
	${libdir}/libnl-idiag-3.a \
	${libdir}/libnl-cli-3.a \
	${base_libdir}/libnl-genl-3.a \
    "

PACKAGES += "${PN}-genl"

FILES_${PN}-genl  = " \
	${libdir}/libnl-genl-3.so.* \
	${libdir}/libnl-genl.so.* \
	${sbindir}/genl-ctrl-list \
    "

RREPLACES_${PN}-genl = "libnl-genl2 libnl-genl-3-200"
RCONFLICTS_${PN}-genl = "libnl-genl2 libnl-genl-3-200"
