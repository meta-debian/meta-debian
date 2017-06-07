#
# base recipe: meta-openembedded/meta-networking/recipes-extended/iscsitarget/iscsitarget_1.4.20.3+svn502.bb
# base branch: master
# base commit: 5c1e38e172607f1302a00f288aaa737ded36d1c7
#

SUMMARY = "iSCSI Enterprise Target userland tools"
DESCRIPTION = "\
iSCSI Enterprise Target is for building an iSCSI storage system on \
Linux. It is aimed at developing an iSCSI target satisfying enterprise \
requirements. \
"
HOMEPAGE = "http://iscsitarget.sourceforge.net/"
PR = "r1"
inherit debian-package
PV = "1.4.20.3+svn502"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=6e233eda45c807aa29aeaa6d94bc48a2"

inherit module
DEPENDS += "dpkg-native openssl virtual/kernel"

SRC_URI += "file://use-kernel-makefile-to-get-kernel-version.patch \
            file://fix-errors-observed-with-linux-3.19-and-greater.patch \
            file://access-sk_v6_daddr-iff-IPV6-defined.patch \
            file://build_with_updated_bio_struct_of_linux_v4.3_and_above.patch \
            file://build_with_updated_interfaces_of_linux_v4.8_and_above.patch \
            file://fix-call-trace-of-ahash-API-calling.patch"

# skip error checking because debian/patches/series is empty
debian_patch_quilt() {
	# apply patches
	if [ -s ${DEBIAN_QUILT_PATCHES}/series ]; then
	   QUILT_PATCHES=${DEBIAN_QUILT_PATCHES} quilt --quiltrc /dev/null push -a
	fi

	# avoid conflict with "do_patch"
	if [ -d ${DEBIAN_QUILT_DIR} ]; then
		mv ${DEBIAN_QUILT_DIR} ${DEBIAN_QUILT_DIR_ESC}
	fi
}

do_configure[noexec] = "1"

# make_scripts requires kernel source directory to create
# kernel scripts
do_make_scripts[depends] += "virtual/kernel:do_shared_workdir"

do_compile() {
	oe_runmake KSRC=${STAGING_KERNEL_DIR} LDFLAGS='' V=1 kernel
	oe_runmake KSRC=${STAGING_KERNEL_DIR} usr
}
do_install() {
	# Module
	install -d ${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/iscsi
	install -m 0644 ${S}/kernel/iscsi_trgt.ko \
	${D}/lib/modules/${KERNEL_VERSION}/kernel/drivers/iscsi/iscsi_trgt.ko

	# Userspace utilities
	install -d ${D}${sbindir}
	install -m 0755 ${S}/usr/ietd ${D}${sbindir}/ietd
	install -m 0755 ${S}/usr/ietadm ${D}${sbindir}/ietadm

	# Config files, init scripts
	install -d -m 0644 ${D}${sysconfdir}/iet
	install -m 0644 ${S}/etc/ietd.conf ${D}/${sysconfdir}/iet/ietd.conf
	install -m 0644 ${S}/etc/initiators.allow \
		${D}${sysconfdir}/iet/initiators.allow
	install -m 0644 ${S}/etc/targets.allow ${D}${sysconfdir}/iet/targets.allow
	install -D -m 0644 ${S}/debian/iscsitarget.default \
		${D}${sysconfdir}/default/iscsitarget
	install -D -m 0755 ${S}/debian/iscsitarget.init \
		${D}${sysconfdir}/init.d/iscsitarget

	DEB_UPSTREAM_VERSION=`dpkg-parsechangelog | sed -rne 's,^Version: ([^-]+).*,\1,p'`
	install -d ${D}${prefix}/src/${PN}-$DEB_UPSTREAM_VERSION/kernel/ \
		${D}${prefix}/src/${PN}-$DEB_UPSTREAM_VERSION/include/ \
		${D}${prefix}/src/${PN}-$DEB_UPSTREAM_VERSION/patches/ \
		${D}${prefix}/src/${PN}-$DEB_UPSTREAM_VERSION/debian

	# Copy only the driver source to the proper location
	cp ${S}/kernel/*.c ${S}/kernel/*.h ${S}/kernel/Makefile \
		${D}${prefix}/src/${PN}-$DEB_UPSTREAM_VERSION/kernel/
	cp ${S}/include/* ${D}${prefix}/src/${PN}-$DEB_UPSTREAM_VERSION/include/
	cp ${S}/patches/* ${D}${prefix}/src/${PN}-$DEB_UPSTREAM_VERSION/patches/

	cp ${S}/debian/control.modules.in \
		${D}${prefix}/src/${PN}-$DEB_UPSTREAM_VERSION/debian/control.in

	# install debian/ files
	cd ${S}/debian
	cp changelog control compat *.modules.in rules copyright \
		*-module-* ${D}${prefix}/src/${PN}-$DEB_UPSTREAM_VERSION/debian

	# create toplevel module Makefile
	echo "obj-m = kernel/" > ${D}${prefix}/src/${PN}-$DEB_UPSTREAM_VERSION/Makefile

	# Prepare dkms.conf from the dkms.conf.in template
	sed "s/__VERSION__/$DEB_UPSTREAM_VERSION/g" ${S}/debian/dkms.conf.in > \
		${D}${prefix}/src/${PN}-$DEB_UPSTREAM_VERSION/dkms.conf

	grep ^PATCH ${S}/dkms.conf >> ${D}${prefix}/src/${PN}-$DEB_UPSTREAM_VERSION/dkms.conf

	rm ${D}${prefix}/src/${PN}-$DEB_UPSTREAM_VERSION/kernel/iscsi_trgt.mod.c
}
PACKAGES =+ "${PN}-dkms"
FILES_${PN}-dkms = "${prefix}/src/*"
FILES_${PN} += "${sysconfdir} ${sbindir}"

RDEPENDS_${PN} += "kernel-module-iscsi-trgt procps lsb-base"
RRECOMMENDS_${PN} = "kernel-module-crc32c kernel-module-libcrc32c"
