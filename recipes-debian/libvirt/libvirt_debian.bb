#
# base recipe: http://git.yoctoproject.org/cgit/cgit.cgi/meta-virtualization/tree/recipes-extended/libvirt/libvirt_1.3.5.bb
# base branch: master
#

SUMMARY = "programs for the libvirt library"
DESCRIPTION = "Libvirt is a C toolkit to interact with the virtualization capabilities \
 of recent versions of Linux (and other OSes). The library aims at providing \
 a long term stable C API for different virtualization mechanisms. It currently \
 supports QEMU, KVM, XEN, OpenVZ, LXC, and VirtualBox."
HOMEPAGE = "http://libvirt.org"

PR = "r0"
inherit debian-package

LICENSE = "GPL-2+ & LGPL-2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LESSER;md5=4fbd65380cdd255951079008b364516c"
# Disable-failing-virnetsockettest_debian.patch
#	Disable failing virnetsockettest: remove some function are defined but not used
# Fix-trailing-curly-brace-from-upstream-patch_debian.patch
#	Remove trailing curly brace in patch without matching closing brace
SRC_URI += "\
	file://Disable-failing-virnetsockettest_debian.patch \
	file://Fix-trailing-curly-brace-from-upstream-patch_debian.patch"

inherit autotools gettext pkgconfig

EXTRA_OECONF += "\
        --with-storage-rbd \
        --with-network \
        --with-init-script=systemd \
        --with-systemd-daemon \
        --with-apparmor --with-secdriver-apparmor --with-apparmor-profiles \
        --disable-silent-rules \
        --disable-rpath \
        --with-qemu-user=libvirt-qemu \
        --with-qemu-group=libvirt-qemu \
        --with-storage-fs \
        --without-esx \
        --without-phyp \
        --enable-debug \
        --without-hal \
        --without-firewalld \
        --without-attr \
	"
DEPENDS += "libnl libxml2 libxslt-native apparmor audit"
PACKAGECONFIG ??= "box udev audit libcap-ng macvtap qemu storage-lvm storage-disk"

PACKAGECONFIG[numa] = "--with-numactl,--without-numactl,numactl,"
PACKAGECONFIG[avahi] = "--with-avahi,--without-avahi,avahi,"
PACKAGECONFIG[netcf] = "--with-netcf,--without-netcf,netcf,"
PACKAGECONFIG[yajl] = "--with-yajl,--without-yajl,yajl,"
PACKAGECONFIG[xen] = "--with-xen,--without-xen,xen,"
PACKAGECONFIG[libxl] = "--with-libxl,--without-libxl,xen,"
PACKAGECONFIG[sanlock] = "--with-sanlock,--without-sanlock,sanlock,"
PACKAGECONFIG[polkit] = "--with-polkit,--without-polkit,policykit-1,"
PACKAGECONFIG[ssh2] = "--with-ssh2,--without-ssh2,libssh2,"
PACKAGECONFIG[dtrace] = "--with-dtrace,--without-dtrace,systemtap,"
PACKAGECONFIG[storage-iscsi] = "--with-storage-iscsi,--without-storage-iscsi,open-iscsi,"
PACKAGECONFIG[storage-sheepdog] = "--with-storage-sheepdog,--without-storage-sheepdog,sheepdog,"
PACKAGECONFIG[storage-lvm] = "--with-storage-lvm,--without-storage-lvm,lvm2 lvm2-native parted,"
PACKAGECONFIG[storage-disk] = "--with-storage-disk,--without-storage-disk,lvm2 lvm2-native parted,"
PACKAGECONFIG[vbox] = "--with-vbox,--without-vbox,,"
PACKAGECONFIG[udev] = "--with-udev --with-pciaccess,--without-udev,udev libpciaccess,"
PACKAGECONFIG[audit] = "--with-audit,--without-audit,audit,"
PACKAGECONFIG[libcap-ng] = "--with-capng,--without-capng,libcap-ng,"
PACKAGECONFIG[macvtap] = "--with-macvtap,--without-macvtap,libnl,"
PACKAGECONFIG[sasl] = "--with-sasl,--without-sasl,libsasl2,"
PACKAGECONFIG[qemu] = "--with-qemu,--without-qemu,qemu,"
PACKAGECONFIG[openvz] = "--with-openvz,--without-openvz,,"
PACKAGECONFIG[selinux] = "--with-selinux,--without-selinux,libselinux,"
PACKAGECONFIG[fuse] = "--with-fuse,--without-fuse,fuse,"
# Avoid a parallel build problem
PARALLEL_MAKE = ""

do_install_append() {
	install -d ${D}${systemd_system_unitdir}
	install -d ${D}${sysconfdir}/init.d ${D}${sysconfdir}/default
	cp -a ${D}${libdir}/systemd/system/* ${D}${systemd_system_unitdir}
	install -m 0644 ${S}/debian/libvirt-daemon-system.libvirtd.default \
		${D}${sysconfdir}/default/libvirtd
	install -m 0644 ${S}/tools/libvirt-guests.sysconf \
		${D}${sysconfdir}/default/libvirt-guests
	install -m 0755 ${S}/debian/libvirt-daemon-system.libvirtd.init \
		${D}${sysconfdir}/init.d/libvirtd
	install -m 0755 ${B}/tools/libvirt-guests.sh \
		${D}${sysconfdir}/init.d/libvirt-guests
	install -D -m 0644 ${S}/debian/polkit/60-libvirt.rules \
		${D}${datadir}/polkit-1/rules.d

	rm -rf  ${D}${libdir}/systemd \
		${D}${sysconfdir}/sysconfig \
		${D}${libdir}/sysctl.d \
		${D}${localstatedir}/run
}
ALLOW_EMPTY_${DPN}-bin = "1"
PACKAGES =+ "${DPN}-bin ${DPN}-daemon-system ${DPN}-clients ${DPN}-daemon"
FILES_${DPN}-clients = "\
	${sysconfdir}/${DPN}/libvirt.conf \
	${sysconfdir}/${DPN}/virt-login-shell.conf \
	${bindir}/*"
FILES_${DPN}-daemon = "\
	${libdir}/${DPN}/connection-driver/*.so \
	${libdir}/${DPN}/libvirt-guests.sh \
	${libdir}/${DPN}/libvirt_iohelper \
	${libdir}/${DPN}/libvirt_leaseshelper \
	${libdir}/${DPN}/libvirt_lxc \
	${libdir}/${DPN}/libvirt_parthelper \
	${libdir}/${DPN}/libvirt_sanlock_helper \
	${libdir}/${DPN}/lock-driver/*.so \
	${sbindir}/libvirtd* \
	${datadir}/augeas ${datadir}/${DPN}"
FILES_${DPN}-daemon-system = "\
	${sysconfdir}/* \
	${systemd_system_unitdir} \
	${libdir}/${DPN}/virt-aa-helper \
	${datadir}/polkit-1/* \
	${localstatedir}/lib/*"
FILES_${PN}-dbg += "\
	${libdir}/${DPN}/connection-driver/.debug \
	${libdir}/${DPN}/lock-driver/.debug"

# Follow debian/control
RDEPENDS_${DPN}-bin += "${DPN}-daemon-system ${DPN}-clients"
RDEPENDS_${DPN}-daemon-system += "\
	adduser ${DPN}-clients ${DPN}-daemon logrotate"
