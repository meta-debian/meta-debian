#
# no base recipe, full scratch
#

SUMMARY = "A System and service manager"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/systemd"

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = " \
file://LICENSE.GPL2;md5=751419260aa954499f7abaabaa882bbe \
file://LICENSE.LGPL2.1;md5=4fbd65380cdd255951079008b364516c \
"
PROVIDES = "udev"

inherit debian-package
PV = "215"
PR = "r2"

inherit pkgconfig autotools useradd python3native

SRC_URI += "file://0001-Add-include-macro.h-to-mtd_probe.h.patch"

DEPENDS = "intltool-native \
           gperf-native \
           libcap \
           dbus \
           glib-2.0 \
           acl \
           xz-utils \
           libgcrypt \
           kmod \
           util-linux \
           ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam', '', d)} \
           python3-lxml-native \
          "

# These options are almost same as CONFFLAGS in debian/rules.
# Other target specific CONFFLAGS in debian/rules are ignored.
DEBIAN_CONFOPTS = "--with-rootprefix=${base_prefix} \
                   --with-rootlibdir=${base_libdir} \
                   --with-sysvinit-path=${sysconfdir}/init.d \
                   --with-sysvrcnd-path=${sysconfdir} \
                   --with-firmware-path=/lib/firmware \
                   --with-ntp-servers="" \
                   --with-dns-servers="" \
                   --with-system-uid-max=999 \
                   --with-system-gid-max=999 \
                   --disable-coredump \
                   --disable-efi \
                   --disable-myhostname \
                   --disable-vconsole \
                   --disable-microhttpd \
                   --disable-sysusers \
                   --disable-silent-rules \
                   PYTHON="${PYTHON}" \
                  "

# --enable-dependency-tracking:
#   avoid compile error "Cannot open src/*/org.freedesktop.*.policy"
# --disable-selinux: Disable selinux support
EXTRA_OECONF = "${DEBIAN_CONFOPTS} \
                ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '--enable-pam', '--disable-pam', d)} \
                --disable-manpages \
                --disable-gtk-doc-html \
                --disable-selinux \
                --enable-dependency-tracking \
                --enable-compat-libs \
                --disable-libcryptsetup \
               "

PACKAGECONFIG ??= "audit"
PACKAGECONFIG[audit] = "--enable-audit,--disable-audit,audit"

do_configure_prepend() {
	export KMOD="${base_bindir}/kmod"
}

# append debian extra files and remove unneeded files
do_install_append() {
	# systemd package: setup base_bindir
	ln -s ${base_libdir}/systemd/systemd ${D}${base_bindir}
	# systemd package: setup sysconfdir
	ln -s ../modules ${D}${sysconfdir}/modules-load.d/modules.conf
	ln -s ../sysctl.conf ${D}${sysconfdir}/sysctl.d/99-sysctl.conf
	for dir in binfmt.d kernel tmpfiles.d udev/rules.d udev/hwdb.d; do
		rm -r ${D}${sysconfdir}/${dir}
	done
	for dir in network ntp-units.d system user; do
		rm -r ${D}${sysconfdir}/systemd/${dir}
	done
	rm ${D}${sysconfdir}/init.d/README
	# systemd package: setup base_libdir
	install -d ${D}${base_libdir}/lsb/init-functions.d
	install -m 0644 ${S}/debian/init-functions.d/40-systemd \
		${D}${base_libdir}/lsb/init-functions.d
	for script in debian-fixup systemd-logind-launch; do
		install -m 0755 ${S}/debian/${script} \
			${D}${base_libdir}/systemd
	done
	for name in system-shutdown system-sleep systemd-update-done; do
		rm -r ${D}${base_libdir}/systemd/${name}
	done
	# systemd package: setup bindir
	rm ${D}${bindir}/kernel-install
	# systemd package: setup libdir
	for dir in binfmt.d kernel modules-load.d rpm sysctl.d \
	           systemd/network systemd/user-generators \
	           tmpfiles.d/etc.conf; do
		rm -r ${D}${libdir}/${dir}
	done
	install -m 0644 ${S}/debian/tmpfiles.d/debian.conf \
		${D}${libdir}/tmpfiles.d
	# systemd package: setup datadir
	for comp in kernel-install udevadm; do
		rm ${D}${datadir}/bash-completion/completions/${comp}
	done
	mv ${D}${datadir}/zsh/site-functions \
		${D}${datadir}/zsh/vendor-completions
	rm ${D}${datadir}/zsh/vendor-completions/_kernel-install
	# systemd package: remove localstatedir
	rm -r ${D}${localstatedir}
	# systemd package: setup ${base_libdir}/systemd/system
	SYSTEMDIR=${D}${base_libdir}/systemd/system
	for service in bootlogd bootlogs bootmisc checkfs checkroot-bootclean \
	               checkroot cryptdisks-early cryptdisks fuse halt \
	               hostname hwclock hwclockfirst killprocs motd \
	               mountall-bootclean mountall mountdevsubfs mountkernfs \
	               mountnfs-bootclean mountnfs reboot rmnologin sendsigs \
	               single stop-bootlogd-single stop-bootlogd umountfs \
	               umountnfs umountroot x11-common; do
		ln -s /dev/null ${SYSTEMDIR}/${service}.service
	done
	for service in debian-fixup extra/getty-static extra/hwclock-save \
	               ifup@ extra/systemd-setup-dgram-qlen; do
		install -m 0644 ${S}/debian/${service}.service ${SYSTEMDIR}
	done
	for wants in graphical multi-user poweroff reboot rescue; do
		install -d ${SYSTEMDIR}/${wants}.target.wants
		ln -s ../systemd-update-utmp-runlevel.service \
			${SYSTEMDIR}/${wants}.target.wants
	done
	install -d ${SYSTEMDIR}/getty.target.wants
	ln -s ../getty-static.service ${SYSTEMDIR}/getty.target.wants
	ln -s systemd-modules-load.service ${SYSTEMDIR}/kmod.service
	ln -s systemd-modules-load.service \
		${SYSTEMDIR}/module-init-tools.service
	install -d ${SYSTEMDIR}/networking.service.d
	install -m 0644 ${S}/debian/extra/network-pre.conf \
		${SYSTEMDIR}/networking.service.d
	ln -s systemd-sysctl.service ${SYSTEMDIR}/procps.service
	ln -s rc-local.service ${SYSTEMDIR}/rc.local.service
	ln -s ../debian-fixup.service ${SYSTEMDIR}/sysinit.target.wants
	for service in ldconfig systemd-journal-catalog-update \
	               systemd-udev-hwdb-update systemd-update-done; do
		rm ${SYSTEMDIR}/sysinit.target.wants/${service}.service
	done
	for service in ldconfig systemd-journal-catalog-update \
	               systemd-udev-hwdb-update systemd-update-done; do
		rm ${SYSTEMDIR}/${service}.service
	done
	ln -s systemd-random-seed.service ${SYSTEMDIR}/urandom.service

	# udev package
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/debian/udev.init ${D}${sysconfdir}/init.d/udev
	install -m 0755 ${S}/debian/udev.udev-finish.init \
		${D}${sysconfdir}/init.d/udev-finish
	install -d ${D}${sysconfdir}/modprobe.d
	install -m 0644 ${S}/debian/extra/fbdev-blacklist.conf \
		${D}${sysconfdir}/modprobe.d/fbdev-blacklist.conf
	install -d ${D}${base_libdir}/systemd/system/sysinit.target.wants
	install -m 0644 ${S}/debian/extra/udev-finish.service \
		${D}${base_libdir}/systemd/system
	ln -s ../udev-finish.service \
		${D}${base_libdir}/systemd/system/sysinit.target.wants
	ln -s systemd-udevd.service \
		${D}${base_libdir}/systemd/system/udev.service
	for script in dsl-modem.agent logger.agent net.agent \
	              udev-finish write_net_rules; do
		install -m 0755 ${S}/debian/extra/${script} \
			${D}${base_libdir}/udev
	done
	for funcs in hotplug rule_generator; do
		install -m 0644 ${S}/debian/extra/${funcs}.functions \
			${D}${base_libdir}/udev
	done
	for rules in 73-idrac 75-persistent-net-generator 80-networking; do
		install -m 0644 ${S}/debian/extra/rules/${rules}.rules \
			${D}${base_libdir}/udev/rules.d
	done
	install -d ${D}${base_sbindir}
	ln -s ${base_bindir}/udevadm ${D}${base_sbindir}
	ln -s ${base_libdir}/systemd/systemd-udevd ${D}${base_sbindir}/udevd

	# systemd-sysv
	ln -s /bin/systemd   ${D}${base_sbindir}/init
	ln -s /bin/systemctl ${D}${base_sbindir}/halt
	ln -s /bin/systemctl ${D}${base_sbindir}/poweroff
	ln -s /bin/systemctl ${D}${base_sbindir}/reboot
	ln -s /bin/systemctl ${D}${base_sbindir}/runlevel
	ln -s /bin/systemctl ${D}${base_sbindir}/shutdown
	ln -s /bin/systemctl ${D}${base_sbindir}/telinit

	# remove .so for deprecated compatibility libraries
	rm -f ${D}${libdir}/libsystemd-daemon.*
	rm -f ${D}${libdir}/libsystemd-login.*
	rm -f ${D}${libdir}/libsystemd-id128.*
	rm -f ${D}${libdir}/libsystemd-journal.*
	rm -f ${D}${base_libdir}/libsystemd-daemon.*
	rm -f ${D}${base_libdir}/libsystemd-login.*
	rm -f ${D}${base_libdir}/libsystemd-id128.*
	rm -f ${D}${base_libdir}/libsystemd-journal.*

	# remove unwanted files
	rm -rf `find ${D}${libdir} -type d -name "__pycache__"`
}

# the following nonessential packages are excluded
#   deprecated library packages like libsystemd-id128-0
#   systemd-sysv: links and manuals for replacing sysvinit
PACKAGES =+ "libsystemd-dev \
             libsystemd0 \
             ${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam-systemd', '', d)} \
             udev \
             libudev-dev \
             libudev1 \
             libgudev-1.0-dev \
             libgudev-1.0-0 \
             systemd-sysv \
             libsystemd-daemon-dev \
             libsystemd-login-dev \
             libsystemd-id128-dev \
             libsystemd-journal-dev \
             python3-systemd \
            "

FILES_python3-systemd = "${PYTHON_SITEPACKAGES_DIR}/systemd/*"
FILES_libsystemd-daemon-dev = "${libdir}/pkgconfig/libsystemd-daemon.pc"
FILES_libsystemd-login-dev = "${libdir}/pkgconfig/libsystemd-login.pc"
FILES_libsystemd-id128-dev = "${libdir}/pkgconfig/libsystemd-id128.pc"
FILES_libsystemd-journal-dev = "${libdir}/pkgconfig/libsystemd-journal.pc"

FILES_${PN} = "${base_bindir} \
               ${bindir} \
               ${base_libdir} \
               ${libdir} \
               ${datadir} \
               ${sysconfdir}/dbus-1 \
               ${sysconfdir}/modules-load.d/modules.conf \
               ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '${sysconfdir}/pam.d/systemd-user', '', d)} \
               ${sysconfdir}/sysctl.d/99-sysctl.conf \
               ${sysconfdir}/systemd \
               ${sysconfdir}/xdg \
              "
FILES_${PN}-dbg += "${base_libdir}/systemd/.debug \
                    ${base_libdir}/systemd/system-generators/.debug \
                    ${base_libdir}/udev/.debug \
                    ${@bb.utils.contains('DISTRO_FEATURES', 'pam', '${base_libdir}/security/.debug/pam_systemd.so', '', d)} \
                    ${PYTHON_SITEPACKAGES_DIR}/systemd/.debug \
                   "
FILES_${PN}-dev = ""
ALLOW_EMPTY_${PN}-dev = "1"

FILES_libsystemd0 = "${base_libdir}/libsystemd.so.*"
FILES_libsystemd-dev = "${libdir}/libsystemd.so \
                        ${libdir}/libsystemd.la \
                        ${libdir}/pkgconfig/libsystemd.pc \
                        ${includedir}/systemd \
                       "

FILES_libpam-systemd = "${base_libdir}/security/pam_systemd.so \
                        ${datadir}/pam-configs/systemd \
                       "

# the following nonessential files are excluded
#   upstart config files (${sysconfdir}/init)
#   build scripts for initramfs-tools (${datadir}/initramfs-tools)
FILES_udev = "${base_bindir}/udevadm \
              ${base_sbindir}/udevadm \
              ${base_sbindir}/udevd \
              ${base_libdir}/systemd/system/sockets.target.wants/systemd-udevd-control.socket \
              ${base_libdir}/systemd/system/sockets.target.wants/systemd-udevd-kernel.socket \
              ${base_libdir}/systemd/system/sysinit.target.wants/systemd-udev-trigger.service \
              ${base_libdir}/systemd/system/sysinit.target.wants/systemd-udevd.service \
              ${base_libdir}/systemd/system/sysinit.target.wants/udev-finish.service \
              ${base_libdir}/systemd/system/systemd-udev-settle.service \
              ${base_libdir}/systemd/system/systemd-udev-trigger.service \
              ${base_libdir}/systemd/system/systemd-udevd-control.socket \
              ${base_libdir}/systemd/system/systemd-udevd-kernel.socket \
              ${base_libdir}/systemd/system/systemd-udevd.service \
              ${base_libdir}/systemd/system/udev-finish.service \
              ${base_libdir}/systemd/system/udev.service \
              ${base_libdir}/systemd/systemd-udevd \
              ${base_libdir}/udev/accelerometer \
              ${base_libdir}/udev/ata_id \
              ${base_libdir}/udev/cdrom_id \
              ${base_libdir}/udev/collect \
              ${base_libdir}/udev/dsl-modem.agent \
              ${base_libdir}/udev/hotplug.functions \
              ${base_libdir}/udev/hwdb.d \
              ${base_libdir}/udev/logger.agent \
              ${base_libdir}/udev/mtd_probe \
              ${base_libdir}/udev/net.agent \
              ${base_libdir}/udev/rule_generator.functions \
              ${base_libdir}/udev/rules.d/42-usb-hid-pm.rules \
              ${base_libdir}/udev/rules.d/50-* \
              ${base_libdir}/udev/rules.d/60-* \
              ${base_libdir}/udev/rules.d/61-accelerometer.rules \
              ${base_libdir}/udev/rules.d/64-btrfs.rules \
              ${base_libdir}/udev/rules.d/70-power-switch.rules \
              ${base_libdir}/udev/rules.d/73-idrac.rules \
              ${base_libdir}/udev/rules.d/75-* \
              ${base_libdir}/udev/rules.d/78-sound-card.rules \
              ${base_libdir}/udev/rules.d/80-* \
              ${base_libdir}/udev/rules.d/95-udev-late.rules \
              ${base_libdir}/udev/scsi_id \
              ${base_libdir}/udev/udev-finish \
              ${base_libdir}/udev/v4l_id \
              ${base_libdir}/udev/write_net_rules \
              ${sysconfdir}/init.d \
              ${sysconfdir}/modprobe.d \
              ${sysconfdir}/udev/udev.conf \
              ${datadir}/pkgconfig/udev.pc \
             "

FILES_libudev1 = "${base_libdir}/libudev.so.*"
FILES_libudev-dev = "${includedir}/libudev.h \
                     ${libdir}/libudev.so \
                     ${libdir}/libudev.la \
                     ${libdir}/libudev.a \
                     ${libdir}/pkgconfig/libudev.pc \
                    "

FILES_libgudev-1.0-0 = "${libdir}/libgudev*.so.*"
FILES_libgudev-1.0-dev = "${includedir}/gudev* \
                      ${libdir}/libgudev*.so \
                      ${libdir}/libgudev*.la \
                      ${libdir}/libgudev*.a \
                      ${libdir}/pkgconfig/gudev*.pc \
                     "
FILES_systemd-sysv = " ${base_sbindir}/init \
                       ${base_sbindir}/halt \
                       ${base_sbindir}/poweroff \
                       ${base_sbindir}/reboot \
                       ${base_sbindir}/runlevel \
                       ${base_sbindir}/shutdown \
                       ${base_sbindir}/telinit \
                     "
RDEPENDS_${PN} += "systemd-sysv sysv-rc"
RDEPENDS_libsystemd-daemon-dev  += "libsystemd-dev"
RDEPENDS_libsystemd-login-dev   += "libsystemd-dev"
RDEPENDS_libsystemd-id128-dev   += "libsystemd-dev"
RDEPENDS_libsystemd-journal-dev += "libsystemd-dev"

RRECOMMENDS_${PN} += "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'libpam-systemd', '', d)}"

# init script requires init-functions, procps's ps, and mountpoint
RDEPENDS_udev += "lsb-base procps sysvinit-mountpoint"

inherit update-alternatives

ALTERNATIVE_${PN} = "init halt reboot shutdown poweroff runlevel"

ALTERNATIVE_TARGET[init] = "${base_bindir}/systemd"
ALTERNATIVE_LINK_NAME[init] = "${base_sbindir}/init"
ALTERNATIVE_PRIORITY[init] ?= "300"

ALTERNATIVE_TARGET[halt] = "${base_bindir}/systemctl"
ALTERNATIVE_LINK_NAME[halt] = "${base_sbindir}/halt"
ALTERNATIVE_PRIORITY[halt] ?= "300"

ALTERNATIVE_TARGET[reboot] = "${base_bindir}/systemctl"
ALTERNATIVE_LINK_NAME[reboot] = "${base_sbindir}/reboot"
ALTERNATIVE_PRIORITY[reboot] ?= "300"

ALTERNATIVE_TARGET[shutdown] = "${base_bindir}/systemctl"
ALTERNATIVE_LINK_NAME[shutdown] = "${base_sbindir}/shutdown"
ALTERNATIVE_PRIORITY[shutdown] ?= "300"

ALTERNATIVE_TARGET[poweroff] = "${base_bindir}/systemctl"
ALTERNATIVE_LINK_NAME[poweroff] = "${base_sbindir}/poweroff"
ALTERNATIVE_PRIORITY[poweroff] ?= "300"

ALTERNATIVE_TARGET[runlevel] = "${base_bindir}/systemctl"
ALTERNATIVE_LINK_NAME[runlevel] = "${base_sbindir}/runlevel"
ALTERNATIVE_PRIORITY[runlevel] ?= "300"

DEBIAN_NOAUTONAME = "1"

USERADD_PACKAGES = "${PN}"
USERADD_PARAM_${PN} +=  "--system --no-create-home --home /run/systemd --shell /bin/false --user-group systemd-timesync; --system --no-create-home --home /run/systemd/netif --shell /bin/false --user-group systemd-network; --system --no-create-home --home /run/systemd/resolve --shell /bin/false --user-group systemd-resolve; --system --no-create-home --home /run/systemd --shell /bin/false --user-group systemd-bus-proxy"
GROUPADD_PARAM_${PN} += "--system systemd-journal"
