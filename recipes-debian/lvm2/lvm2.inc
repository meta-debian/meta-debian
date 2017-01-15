INC_PR = "r0"

inherit debian-package
PV = "2.02.111"

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f \
	file://COPYING.LIB;md5=fbc093901857fcd118f065f900982c24 \
"

inherit autotools-brokensep pkgconfig

# Configure follow Debian
# --disable-selinux: Don't use selinux support
EXTRA_OECONF += " \
	--exec-prefix= \
	--sbindir=${base_sbindir} \
	--libdir=${base_libdir} \
	--with-usrlibdir=${libdir} \
	--with-cache=internal \
	--with-cluster=internal \
	--with-device-uid=0 \
	--with-device-gid=6 \
	--with-device-mode=0660 \
	--with-default-run-dir=/run/lvm \
	--with-default-locking-dir=/run/lock/lvm \
	--with-thin=internal \
	--with-thin-check=${sbindir}/thin_check \
	--with-thin-dump=${sbindir}/thin_dump \
	--with-thin-repair=${sbindir}/thin_repair \
	--enable-applib \
	--enable-cmdlib \
	--enable-dmeventd \
	--enable-lvmetad \
	--enable-pkgconfig \
	--enable-readline \
	--disable-blkdeactivate \
	--disable-selinux \
"

DEVMAPPER_ABINAME = "1.02.1"
EXTRA_OEMAKE += "LIB_VERSION_DM=${DEVMAPPER_ABINAME}"

PACKAGES =+ "clvm dmeventd dmsetup libdevmapper-dev libdevmapper-event libdevmapper \
	    liblvm2app liblvm2cmd"

FILES_clvm = "${sbindir}/clvmd \
              ${sysconfdir}/init.d/clvm \
              ${systemd_unitdir}/lvm2-cluster* \
              ${systemd_unitdir}/system/lvm2-cluster* \
              ${systemd_unitdir}/system/lvm2-clvmd*"
FILES_dmeventd = "${base_sbindir}/dmeventd \
                  ${base_libdir}/device-mapper/* \
                  ${systemd_unitdir}/system/dm-event*"
FILES_dmsetup = "${base_sbindir}/dmsetup \
                 ${base_libdir}/udev/rules.d/*-dm*.rules"
FILES_libdevmapper-dev = "${libdir}/libdevmapper*${SOLIBSDEV} \
                          ${libdir}/pkgconfig/devmapper*.pc \
                          ${includedir}/libdevmapper*"
FILES_libdevmapper-event = "${base_libdir}/libdevmapper-event${SOLIBS}"
FILES_libdevmapper = "${base_libdir}/libdevmapper${SOLIBS}"
FILES_liblvm2app = "${base_libdir}/liblvm2app${SOLIBS}"
FILES_liblvm2cmd = "${base_libdir}/liblvm2cmd${SOLIBS}"
FILES_${PN}-dbg += "${base_libdir}/device-mapper/.debug"
FILES_${PN} += "${systemd_unitdir}/system/lvm2*"

DEBIANNAME_${PN}-dev = "liblvm2-dev"
