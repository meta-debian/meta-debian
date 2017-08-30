SUMMARY = "maintain multipath block device access"
DESCRIPTION = "\
	These tools are in charge of maintaining the disk multipath device maps and \
	react to path and map events. \
	If you install this package you may have to change the way you address block \
	devices. See README.Debian for details. \
"
HOMEPAGE = "http://christophe.varoqui.free.fr/"
PR = "r0"
inherit debian-package
PV = "0.5.0"

LICENSE = "LGPLv2+ & GPLv2+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=7be2873b6270e45abacc503abbe2aa3d \
	file://libmultipath/parser.c;beginline=1;endline=18;md5=4207fa3fb9902e3dd287cd489545c80b"
inherit autotools-brokensep
DEPENDS += "libaio lvm2 readline"

#follow debian/rules
export DISABLE_SYSTEMD = "1"
EXTRA_OEMAKE = "OPTFLAGS="-pipe -Wall -Wunused -Wstrict-prototypes" LIB=lib"

#install follow Debian jessie
do_install() {
	oe_runmake install LIB=lib DESTDIR=${D}
	
	install -m 0755 ${S}/debian/multipath-tools.init \
		${D}${sysconfdir}/init.d/multipath-tools
	install -m 0755 ${S}/debian/multipath-tools.multipath-tools-boot.init \
		${D}${sysconfdir}/init.d/multipath-tools-boot
	install -m 0755 ${S}/debian/dmsetup_env ${D}${base_libdir}/udev/
	install -D -m 0644 ${S}/kpartx/kpartx.rules \
		${D}${base_libdir}/udev/rules.d/60-kpartx.rules
	install -m 0644 ${S}/debian/multipath.udev \
		${D}${base_libdir}/udev/rules.d/60-multipath.rules
	
	# initramfs stuff:
	install -D -m 755 ${S}/debian/initramfs/hooks \
		${D}${datadir}/initramfs-tools/hooks/multipath
	install -D -m 755 ${S}/debian/initramfs/local-top \
		${D}${datadir}/initramfs-tools/scripts/local-top/multipath
	install -D -m 755 ${S}/debian/initramfs/init-top \
		${D}${datadir}/initramfs-tools/scripts/init-top/multipath
	
	# reportbug:
	for pkg in "multipath-tools" "multipath-tools-boot"; do \
		install -D -m 755 ${S}/debian/reportbug/script \
			${D}${datadir}/bug/$pkg/script; \
	done
	
	rm ${D}${sysconfdir}/udev/rules.d/kpartx.rules
	LINKLIB=$(basename $(readlink ${D}${base_libdir}/libmpathpersist.so))
	rm ${D}${base_libdir}/libmpathpersist.so
	ln -sf $LINKLIB ${D}${base_libdir}/libmpathpersist.so
}
PACKAGES =+ "kpartx ${PN}-boot"

FILES_kpartx = "\
	${base_libdir}/udev/kpartx ${base_libdir}/udev/kpartx_id \
	${base_libdir}/udev/rules.d/60-kpartx.rules ${base_sbindir}/kpartx \
	${base_libdir}/udev/dmsetup_env \
"
FILES_${PN} += "\
	${datadir}/bug/multipath-tools ${base_libdir}/multipath \
"
FILES_${PN}-boot = "\
	${datadir}/bug/multipath-tools-boot ${datadir}/initramfs-tools \
"
FILES_${PN}-dbg += "${base_libdir}/multipath/.debug/*"

#follow debian/control
RDEPENDS_${PN} += "sysvinit-initscripts udev"
RDEPENDS_kpartx += "dmsetup udev"
RDEPENDS_${PN}-boot += "${PN}"
