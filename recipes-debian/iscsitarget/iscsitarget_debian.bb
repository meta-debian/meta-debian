#
# base recipe: https://github.com/openembedded/openembedded/tree/\
#              master/recipes/iscsi-target
# base branch: master
#

SUMMARY = "iSCSI Enterprise Target userland tools"
DESCRIPTION = "\
iSCSI Enterprise Target is for building an iSCSI storage system on \
Linux. It is aimed at developing an iSCSI target satisfying enterprise \
requirements. \
"
HOMEPAGE = "http://iscsitarget.sourceforge.net/"
PR = "r0"
inherit debian-package

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=6e233eda45c807aa29aeaa6d94bc48a2"

inherit module
DEPENDS += "dpkg-native"

SRC_URI += "file://use-kernel-makefile-to-get-kernel-version.patch \
            file://fix-errors-observed-with-linux-3.19-and-greater.patch \
            file://access-sk_v6_daddr-iff-IPV6-defined.patch"

# Drop patches, applied upstream (follow debian/changlog)
do_debian_patch[noexec] = "1"
do_configure[noexec] = "1"

do_compile() {
	oe_runmake KSRC=${STAGING_KERNEL_DIR}
}
do_install() {
	# Userspace utilities
	install -d ${D}${sbindir}
	install -m 0755 ${S}/usr/ietd ${D}${sbindir}/ietd
	install -m 0755 ${S}/usr/ietadm ${D}${sbindir}/ietadm

	# Config files, init scripts
	mkdir -p ${D}${sysconfdir}/iet
	install -m 0644 ${S}/etc/ietd.conf ${D}/${sysconfdir}/iet/ietd.conf
	install -m 0644 ${S}/etc/initiators.allow \
		${D}${sysconfdir}/iet/initiators.allow
	install -m 0644 ${S}/etc/targets.allow ${D}${sysconfdir}/iet/targets.allow
	install -D -m 0644 ${S}/debian/iscsitarget.default \
		${D}${sysconfdir}/default/iscsitarget
	install -D -m 0644 ${S}/debian/iscsitarget.init \
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

RDEPENDS_${PN} += "procps lsb-base"
