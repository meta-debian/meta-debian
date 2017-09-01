#
# base recipe: http://git.yoctoproject.org/cgit/cgit.cgi/meta-virtualization/tree/recipes-extended/libvirt/libvirt_1.3.5.bb
# base branch: master
# base commit: 66997c4dcfc5cabd0b357ea46d6ac15ec3163c51
#

SUMMARY = "programs for the libvirt library"
DESCRIPTION = "Libvirt is a C toolkit to interact with the virtualization capabilities \
 of recent versions of Linux (and other OSes). The library aims at providing \
 a long term stable C API for different virtualization mechanisms. It currently \
 supports QEMU, KVM, XEN, OpenVZ, LXC, and VirtualBox."
HOMEPAGE = "http://libvirt.org"

PR = "r0"
inherit debian-package
PV = "1.2.9"

LICENSE = "GPL-2+ & LGPL-2.1+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LESSER;md5=4fbd65380cdd255951079008b364516c"
# Disable-failing-virnetsockettest_debian.patch
#	Disable failing virnetsockettest: remove some function are defined but not used
SRC_URI += "\
	file://Disable-failing-virnetsockettest_debian.patch \
	"

inherit autotools gettext pkgconfig useradd

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
CACHED_CONFIGUREVARS += "\
ac_cv_path_DNSMASQ=${sbindir}/dnsmasq \
ac_cv_path_XMLLINT=${bindir}/xmllint \
ac_cv_path_XMLCATLOG=${bindir}/xmlcatalog \
ac_cv_path_TC=${base_sbindir}/tc \
ac_cv_path_UDEVADM=${base_sbindir}/udevadm \
ac_cv_path_MODPROBE=${base_sbindir}/modprobe \
ac_cv_path_IPTABLES_PATH=${base_sbindir}/iptables \
ac_cv_path_IP6TABLES_PATH=${base_sbindir}/ip6tables \
ac_cv_path_MOUNT=${base_bindir}/mount \
ac_cv_path_UMOUNT=${base_bindir}/umount \
ac_cv_path_PARTED=${base_sbindir}/parted \
ac_cv_path_DMSETUP=${base_sbindir}/dmsetup \
ac_cv_path_IP_PATH=${base_sbindir}/ip \
"

DEPENDS += "libnl libxml2 libxslt-native apparmor audit dnsmasq qemu"
PACKAGECONFIG ??= "box udev audit libcap-ng macvtap qemu storage-lvm storage-disk"

PACKAGECONFIG[libpcap] = "--with-libpcap,--without-libpcap,libpcap,"
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
		${D}${datadir}/polkit-1/rules.d/60-libvirt.rules

	rm -rf  ${D}${libdir}/systemd \
		${D}${sysconfdir}/sysconfig \
		${D}${libdir}/sysctl.d \
		${D}${localstatedir}/run

	# Base on debian/libvirt-daemon-system.dirs
	install -d ${D}${localstatedir}/lib/libvirt/boot \
	           ${D}${localstatedir}/lib/libvirt/images \
	           ${D}${localstatedir}/lib/libvirt/channel/target \
	           ${D}${localstatedir}/lib/libvirt/sanlock \
	           ${D}${localstatedir}/cache/libvirt/qemu \
	           ${D}${localstatedir}/log/libvirt/qemu \
	           ${D}${localstatedir}/log/libvirt/uml \
	           ${D}${localstatedir}/log/libvirt/lxc \
	           ${D}${localstatedir}/lib/polkit-1/localauthority/10-vendor.d/
}
# Base on debian/libvirt-daemon-system.postinst
pkg_postinst_${PN}-daemon-system() {
    add_statoverrides()
    {
        ROOT_DIRS="\
            $D${localstatedir}/lib/libvirt/images/ \
            $D${localstatedir}/lib/libvirt/boot/   \
            $D${localstatedir}/cache/libvirt/      \
        "

        QEMU_DIRS="\
             $D${localstatedir}/lib/libvirt/qemu/   \
             $D${localstatedir}/cache/libvirt/qemu/ \
             $D${localstatedir}/lib/libvirt/qemu/channel/ \
             $D${localstatedir}/lib/libvirt/qemu/channel/target/ \
        "

        SANLOCK_DIR="$D${localstatedir}/lib/libvirt/sanlock"

        QEMU_CONF="$D${sysconfdir}/libvirt/qemu.conf"

        for dir in ${ROOT_DIRS}; do
            [ ! -e "${dir}" ] || chown root:root "${dir}"
            [ ! -e "${dir}" ] || chmod 0711 "${dir}"
        done

        for dir in ${QEMU_DIRS}; do
            [ ! -e "${dir}" ] || chown libvirt-qemu:libvirt-qemu "${dir}"
            [ ! -e "${dir}" ] || chmod 0750 "${dir}"
        done

        [ ! -e "${SANLOCK_DIR}" ] || chown root:root "${SANLOCK_DIR}"
        [ ! -e "${SANLOCK_DIR}" ] || chmod 0700 "${SANLOCK_DIR}"

        [ ! -e "${QEMU_CONF}" ] || chown root:root "${QEMU_CONF}"
        [ ! -e "${QEMU_CONF}" ] || chmod 0600 "${QEMU_CONF}"
    }
    add_statoverrides
    # Make sure the directories don't get removed on package removal since
    # logrotate chokes otherwise.
    for dir in qemu uml lxc; do
        touch $D${localstatedir}/log/libvirt/"${dir}"/.placeholder
    done

    # Force refresh of capabilties (#731815)
    rm -f $D${localstatedir}/cache/libvirt/qemu/capabilities/*.xml
}
USERADD_PACKAGES = "${PN}-daemon-system"
GROUPADD_PARAM_${PN}-daemon-system = "-r libvirt; -r kvm; -r libvirt-qemu"
USERADD_PARAM_${PN}-daemon-system = "-r -g libvirt-qemu -G kvm --home-dir /var/lib/libvirt \
                              --no-create-home libvirt-qemu \
                              "
ALLOW_EMPTY_${PN}-bin = "1"
PACKAGES =+ "${PN}-bin ${PN}-clients ${PN}-daemon-system ${PN}-daemon"
FILES_${PN}-bin = ""
FILES_${PN}-clients = "\
	${sysconfdir}/${PN}/libvirt.conf \
	${sysconfdir}/${PN}/virt-login-shell.conf \
	${bindir}/*"
FILES_${PN}-daemon = "\
	${libdir}/${PN}/connection-driver/*.so \
	${libdir}/${PN}/libvirt-guests.sh \
	${libdir}/${PN}/libvirt_iohelper \
	${libdir}/${PN}/libvirt_leaseshelper \
	${libdir}/${PN}/libvirt_lxc \
	${libdir}/${PN}/libvirt_parthelper \
	${libdir}/${PN}/libvirt_sanlock_helper \
	${libdir}/${PN}/lock-driver/*.so \
	${sbindir}/libvirtd* \
	${sbindir}/virtlockd \
	${datadir}/augeas \
	${datadir}/${PN}/schemas/* \
	${datadir}/${PN}/libvirtLogo.png \
	${datadir}/${PN}/cpu_map.xml"
FILES_${PN}-daemon-system = "\
	${sysconfdir}/* \
	${systemd_system_unitdir} \
	${libdir}/${PN}/virt-aa-helper \
	${datadir}/polkit-1/* \
	${localstatedir}/lib/*"
FILES_${PN}-dbg += "\
	${libdir}/${PN}/connection-driver/.debug \
	${libdir}/${PN}/lock-driver/.debug"
FILES_${PN}-dev += "\
	${libdir}/${PN}/*/*.la \
	${datadir}/${PN}/api/* \
	"
# Follow debian/control
RDEPENDS_${PN}-bin += "${PN}-daemon-system ${PN}-clients"
RDEPENDS_${PN}-daemon-system += "\
	adduser ${PN}-clients ${PN}-daemon logrotate gettext-base"
RRECOMMENDS_${PN}-daemon += "libxml2-utils qemu"
RRECOMMENDS_${PN}-daemon-system += "dmidecode dnsmasq-base iproute2 iptables parted ebtables"
RRECOMMENDS_${PN} += "lvm2"

PKG_${PN} = "${PN}0"
