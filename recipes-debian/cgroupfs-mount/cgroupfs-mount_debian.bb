SUMMARY = "Light-weight package to set up cgroupfs mounts"
DESCRIPTION = "\
 Control groups are a kernel mechanism for tracking and imposing \
 limits on resource usage on groups of tasks. \
 . \
 This package installs scripts to set up cgroups at boot without doing any \
 cgroup management or doing any classification of tasks into cgroups."
HOMEPAGE = "https://github.com/tianon/cgroupfs-mount"

PR = "r0"
inherit debian-package
PV = "1.1"

LICENSE = "GPL-3.0+"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=748441b371c032eda4cfc3e5b703997a \
                    file://cgroupfs-umount;beginline=2;endline=5;md5=e69c19fa6efa953933d3c8784933547d"

do_install() {
	install -d ${D}${bindir}
	install -d ${D}${sysconfdir}/init ${D}${sysconfdir}/init.d

	install -m 0755 ${S}/cgroupfs-mount ${D}${bindir}
	install -m 0755 ${S}/cgroupfs-umount ${D}${bindir}
	install -m 0644 ${S}/debian/cgroupfs-mount.upstart \
		${D}${sysconfdir}/init/cgroupfs-mount.conf
	install -m 0755 ${S}/debian/cgroupfs-mount.init \
		${D}${sysconfdir}/init.d/cgroupfs-mount
}
RDEPENDS_${PN} += "mountall"
