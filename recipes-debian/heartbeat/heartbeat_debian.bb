#
# base recipe: http://git.yoctoproject.org/cgit/cgit.cgi/meta-cgl/tree/meta-cgl-common/recipes-cgl/heartbeat/heartbeat_3.0.5.bb
# base commit: 7bf1b6fd2b63a9a51fa9da81c8e9072fa8a3a7f7
# base branch: master
#

PR = "r0"

SUMMARY = "Messaging and membership subsystem for High-Availability Linux"

inherit debian-package
PV = "3.0.5+hg12629"

LICENSE = "GPLv2 & LGPLv2.1+"
LIC_FILES_CHKSUM = " \
	file://doc/COPYING;md5=c93c0550bd3173f4504b2cbd8991e50b \
	file://doc/COPYING.LGPL;md5=d8045f3b8f929c1cb29a1e3fd737b499 \
"

SRC_URI += " \
	file://membership-ccm-Makefile.am-fix-warning.patch \
	file://Makefile.am-not-chgrp-in-cross-compile.patch \
	file://configure.in-Error-and-warning-fix.patch \
	file://heartbeat-init.d-heartbeat.in-modify-parameter.patch \
	file://disable-build-doc.patch \
"
DEPENDS = "cluster-glue corosync gnutls"

inherit autotools-brokensep pkgconfig useradd

EXTRA_OECONF = " \
	STAGING_DIR_TARGET=${STAGING_DIR_TARGET} \
	--disable-fatal-warnings \
"
SOURCE1 = "heartbeat/init.d/heartbeat"
CFLAGS_append += "-DGLIB_COMPILATION"

do_configure() {
	./bootstrap
	isbigendian=`echo ${TUNE_FEATURES} | grep bigendian` || true
	if [ $isbigendian"x" = "x" ] ; then
		CPU_ENDIAN="little"
	else
		CPU_ENDIAN="big"
	fi
	cp -a ./configure ./configure.orig
	if [ ${CPU_ENDIAN} == "little" ]; then
		sed -e "s@CROSS_ENDIAN_INFO@\$as_echo \"#define CONFIG_LITTLE_ENDIAN 1\" >>confdefs.h@g" \
			-e "s@CROSS_LIBDIR@${_LIBDIR}@g" \
		./configure.orig > ./configure
	else
	sed -e "s@CROSS_ENDIAN_INFO@\$as_echo \"#define CONFIG_BIG_ENDIAN 1\" >>confdefs.h@g" \
		-e "s@CROSS_LIBDIR@${_LIBDIR}@g" \
		./configure.orig > ./configure
	fi
	oe_runconf ${EXTRA_OECONF}
}
do_compile_prepend() {
	sed -i 's|^hardcode_libdir_flag_spec=.*|hardcode_libdir_flag_spec=""|g' ${HOST_PREFIX}libtool
	sed -i 's|^runpath_var=LD_RUN_PATH|runpath_var=DIE_RPATH_DIE|g' ${HOST_PREFIX}libtool
	make clean
}
do_install_append () {
	if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
		install -d ${D}${libexecdir}
		install -m 0755 ${S}/${SOURCE1} ${D}${libexecdir}/heartbeat.init
	fi
	ln -sf ha.d ${D}${sysconfdir}/heartbeat

}

inherit systemd

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "-r haclient"
USERADD_PARAM_${PN} = " \
	-r -g haclient -d /var/lib/heartbeat/cores/hacluster -M \
	-s /sbin/nologin -c \"heartbeat user\" hacluster \
"

PACKAGES =+ "lib${PN} lib${PN}-dev"

FILES_lib${PN} += "${libdir}/libapphb${SOLIBS} \
	${libdir}/libccmclient${SOLIBS} \
	${libdir}/libclm${SOLIBS} \
	${libdir}/libhbclient${SOLIBS}"

FILES_lib${PN}-dev += "${includedir} ${libdir}/*.so ${libdir}/*.la"

FILES_${PN}-staticdev += "${libdir}/heartbeat/plugins/quorum/*.a \
	${libdir}/heartbeat/plugins/tiebreaker/*.a \
	${libdir}/heartbeat/plugins/HBcompress/*.a \
	${libdir}/heartbeat/plugins/HBauth/*.a \
	${libdir}/heartbeat/plugins/HBcomm/*.a \
	${libdir}/heartbeat/plugins/quorum/weight.a"

FILES_${PN}-dbg += " \
	${libdir}/heartbeat/plugins/quorum/.debug \
	${libdir}/heartbeat/plugins/HBauth/.debug \
	${libdir}/heartbeat/plugins/tiebreaker/.debug \
	${libdir}/heartbeat/plugins/HBcomm/.debug \
	${libdir}/heartbeat/plugins/HBcompress/.debug \
"
FILES_${PN} += " \
	run/heartbeat/ccm \
	run/heartbeat/dopd \
"

PKG_lib${PN} = "lib${PN}2"
PKG_lib${PN}-dev = "lib${PN}2-dev"
