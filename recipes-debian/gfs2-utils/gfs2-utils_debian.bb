SUMMARY = "Global File System 2 - filesystem tools"
DESCRIPTION = "The Global File System allows a cluster of machines to concurrently access\n\
 shared storage hardware like SANs or iSCSI and network block devices. GFS\n\
 can be deployed to build high-availability services without the single point\n\
 of failure of a file server.\n\
 .\n\
 This package contains tools for creating and managing global file systems.\n\
 GFS itself is a set of kernel modules."
LICENSE = "GPL-2+"
SECTION = "admin"
DEPENDS = "gettext-minimal-native corosync pkgconfig redhat-cluster"
LIC_FILES_CHKSUM = "file://doc/COPYING.applications;md5=751419260aa954499f7abaabaa882bbe"
HOMEPAGE = "https://pagure.io/gfs2-utils"

PR = "r0"
inherit debian-package
PV = "3.1.3"

inherit autotools gettext

PACKAGES =+ "gfs2-cluster"

FILES_gfs2-cluster = "${sysconfdir}/init.d/gfs2-cluster \
${sbindir}/gfs_control \
${sbindir}/gfs_controld"
RDEPENDS_gfs2-cluster = "cman corosync libccs libcfg libcpg \
libdlmcontrol libfence liblogthread libquorum libsackpt openais"

FILES_${PN} += "/run/cluster"

do_install_append() {
	install -d ${D}${sysconfdir}/init.d/
	install ${S}/gfs2/init.d/gfs2 ${D}${sysconfdir}/init.d/gfs2-utils
	install ${S}/gfs2/init.d/gfs2-cluster ${D}${sysconfdir}/init.d/gfs2-cluster

	# some programs should be in /sbin are
	# in /usr/sbin in cross build environment, so move them
	for f in fsck.gfs2 mkfs.gfs2 mount.gfs2
	do
		mv ${D}${sbindir}/${f} ${D}${base_sbindir}
	done
}
