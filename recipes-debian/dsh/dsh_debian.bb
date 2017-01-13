SUMMARY = "dancer's shell, or distributed shell"
DESCRIPTION = "\
	Executes specified command on a group of computers using remote shell \
	methods such as rsh or ssh. \
	dsh can parallelise job submission using several algorithms, such as using \
	fan-out method or opening as much connections as possible, or using a window \
	of connections at one time.It also supports 'interactive mode' for interactive\
	maintenance of remote hosts."
PR = "r0"
inherit debian-package
PV = "0.25.10"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"
inherit autotools-brokensep gettext pkgconfig
DEBIAN_PATCH_TYPE = "nopatch"

DEPENDS += "libdshconfig"
EXTRA_OECONF += "--sysconfdir=${sysconfdir}/${PN} --mandir=${mandir}"

#install follow Debian jessie
do_install_append() {
	install -d ${D}${libdir}/update-cluster
	install -d ${D}${sysconfdir}/${PN}/group
	install -d ${D}${datadir}/locale/ja/LC_MESSAGES/

	install -m 0644 ${S}/debian/machines.list ${D}${sysconfdir}/${PN}/
	ln -s ../machines.list ${D}${sysconfdir}/${PN}/group/all
	install -m 0755 ${S}/debian/dsh.updatelist ${D}${libdir}/update-cluster/
	install -m 0644 ${S}/po/ja.gmo ${D}${datadir}/locale/ja/LC_MESSAGES/dsh.mo	
}
FILES_${PN} += "${libdir}/* ${datadir}/locale/*"
