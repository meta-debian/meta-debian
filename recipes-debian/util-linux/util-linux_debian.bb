#
# Base recipe: meta/recipes-core/util-linux/util-linux_2.24.1.bb
# Base branch: daisy
# Base commit: 828b6754c205fe6e5cd5f44d1ff50da304b3273d
#

SUMMARY = "A suite of basic system administration utilities"
DESCRIPTION = "Util-linux includes a suite of basic system administration utilities \
commonly found on most Linux systems.  Some of the more important utilities include \
disk partitioning, kernel message management, filesystem creation, and system login."

PR = "r4"

inherit debian-package
PV = "2.25.2"

LICENSE = "GPLv2+ & LGPLv2.1+ & BSD"
LIC_FILES_CHKSUM = " \
file://README.licensing;md5=1715f5ee3e01203ca1e1e0b9ee65918c \
file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
file://Documentation/licenses/COPYING.GPLv2;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
file://Documentation/licenses/COPYING.LGPLv2.1;md5=4fbd65380cdd255951079008b364516c \
file://Documentation/licenses/COPYING.BSD-3;md5=58dcd8452651fc8b07d1f65ce07ca8af \
file://Documentation/licenses/COPYING.UCB;md5=263860f8968d8bafa5392cab74285262 \
file://libuuid/COPYING;md5=b442ffb762cf8d3e9df1b99e0bb4af70 \
file://libmount/COPYING;md5=fb93f01d4361069c5616327705373b16 \
file://libblkid/COPYING;md5=fb93f01d4361069c5616327705373b16"

inherit autotools gettext pkgconfig systemd update-alternatives python-dir
DEPENDS = "zlib ncurses elfutils"
DEPENDS_append_class-native = " lzo-native"
DEPENDS_append_class-nativesdk = " lzo-native"

# init.d/hwclock.sh require lsb-base
RDEPENDS_${PN}_class-target += "lsb-base"

# Follow Debian/rules
# and add configs: --disable-use-tty-group for building successfully.
# --without-selinux: Don't use selinux support
EXTRA_OECONF = "--enable-line --libdir=${base_libdir} \
		--libexecdir=${libdir} --localstatedir=/run \
		--disable-silent-rules --disable-login \
		--disable-nologin --disable-sulogin \
		--disable-last --disable-mesg --disable-mountpoint \
		--disable-kill --disable-eject --disable-chfn-chsh \
		--enable-raw --enable-partx --enable-tunelp\
		--sbindir=${base_sbindir} --disable-use-tty-group \
		--without-selinux"
PACKAGECONFIG_class-target ??= "${@bb.utils.contains('DISTRO_FEATURES', 'pam', 'pam', '', d)}"
PACKAGECONFIG[pam] = "--enable-su --enable-runuser,--disable-su --disable-runuser, libpam,"
# Respect the systemd feature for uuidd
PACKAGECONFIG[systemd] = "--with-systemd --with-systemdsystemunitdir=${systemd_unitdir}/system/, \
			--without-systemd --without-systemdsystemunitdir,systemd"
# Build setpriv requires libcap-ng
PACKAGECONFIG[libcap-ng] = "--enable-setpriv,--disable-setpriv,libcap-ng,"
# Build python bindings for libmount
PACKAGECONFIG[pylibmount] = "--with-python --enable-pylibmount,--without-python --disable-pylibmount,python"
PACKAGECONFIG[slang] = "--with-slang,--without-slang"

# Follow Debian/rules
# Remove some files
# Use Debian's init script
do_install_append () {
	# the version in bsdmainutils seems newer.
	rm -f ${D}${bindir}/look ${D}${datadir}/man/man1/look.1
	rm -f ${D}${bindir}/hexdump ${D}${datadir}/man/man1/hexdump.1

	# and it's less pain to just let bsmainutils deliver col for now.
	rm -f ${D}${bindir}/col* ${D}${datadir}/man/man1/col*.1
	rm -f ${D}${bindir}/ul ${D}${datadir}/man/man1/ul*.1
	rm -f ${D}${bindir}/cal ${D}${datadir}/man/man1/cal.1
	
	# remove *.la files
	rm -f ${D}${libdir}/*.la
	
	#Removed these conflicting ones for now, see Bug#755986
	rm -f ${D}${datadir}/bash-completion/completions/dmesg
	rm -f ${D}${datadir}/bash-completion/completions/renice
	
	#Some files belong to base_bindir
	if [ ! ${D}${bindir} -ef ${D}${base_bindir} ]; then
		install -d ${D}${base_bindir}
		mv ${D}${bindir}/findmnt ${D}${base_bindir}
		mv ${D}${bindir}/mount ${D}${base_bindir}
		mv ${D}${bindir}/umount ${D}${base_bindir}
		mv ${D}${bindir}/dmesg ${D}${base_bindir}
		mv ${D}${bindir}/lsblk ${D}${base_bindir}
		mv ${D}${bindir}/more ${D}${base_bindir}
		mv ${D}${bindir}/tailf ${D}${base_bindir}
		mv ${D}${bindir}/wdctl ${D}${base_bindir}
	fi
	#Some files belong to bindir
	mv ${D}${exec_prefix}/sbin/addpart ${D}${bindir}
	mv ${D}${exec_prefix}/sbin/resizepart ${D}${bindir}
	mv ${D}${exec_prefix}/sbin/partx ${D}${bindir}
	mv ${D}${exec_prefix}/sbin/delpart ${D}${bindir}
	
	# Install init script
	install -d ${D}${sysconfdir}/init.d
	install -m 0755 ${S}/debian/util-linux.hwclock.sh.init \
					${D}${sysconfdir}/init.d/hwclock.sh
	install -m 0755 ${S}/misc-utils/uuidd.rc.in \
					${D}${sysconfdir}/init.d/uuidd

	# install pam
	install -d ${D}${sysconfdir}/pam.d
	install -m 0644 ${S}/debian/util-linux.runuser.pam ${D}${sysconfdir}/pam.d/runuser
	install -m 0644 ${S}/debian/util-linux.runuser-l.pam ${D}${sysconfdir}/pam.d/runuser-l

	# install /etc/default/hwclock
	install -d ${D}${sysconfdir}/default
	install -m 0644 ${S}/debian/util-linux.hwclock.default ${D}${sysconfdir}/default/hwclock

	# create soft link getty to agetty command
	ln -sf agetty ${D}/${base_sbindir}/getty.${DPN}

	# Install files belong to util-linux package follow Debian
	install -m 0755 ${D}${bindir}/isosize ${D}${base_sbindir}
	# perl gets to do rename, not us.
	mv ${D}${bindir}/rename ${D}${bindir}/rename.ul
}

PACKAGES =+ " \
	bsdutils libblkid-dev libblkid1 libmount-dev libmount1 \
	libsmartcols-dev libsmartcols1 libuuid1 mount uuid-dev \
	uuid-runtime ${DPN}-locales"

FILES_bsdutils = "${bindir}/wall ${bindir}/logger ${bindir}/renice \
                  ${bindir}/script ${bindir}/scriptreplay \
                  ${datadir}/bash-completion/completions/logger \
                  ${datadir}/bash-completion/completions/script \
                  ${datadir}/bash-completion/completions/scriptreplay \
                  ${datadir}/bash-completion/completions/wall \
                  "

FILES_libblkid-dev = " \
		${includedir}/blkid/* \
		${libdir}/libblkid.so \
		${libdir}/pkgconfig/blkid.pc"

FILES_libblkid1 = "${base_libdir}/libblkid.so.*"

FILES_libmount-dev = " \
		${includedir}/libmount/libmount.h \
		${libdir}/libmount.so \
		${libdir}/pkgconfig/mount.pc"

FILES_libmount1 = "${base_libdir}/libmount.so*"

FILES_libsmartcols-dev = " \
		${includedir}/libsmartcols/libsmartcols.h \
		${libdir}/libsmartcols.so \
		${libdir}/pkgconfig/smartcols.pc"

FILES_libsmartcols1 = "${base_libdir}/libsmartcols.so*"

FILES_libuuid1 = "${base_libdir}/libuuid.so.*"

FILES_mount = "${base_bindir}/umount ${base_sbindir}/swapon \
	       ${base_sbindir}/swapoff ${base_bindir}/mount* \
               ${base_sbindir}/losetup ${base_bindir}/findmnt \
               ${datadir}/bash-completion/completions/findmnt \
               ${datadir}/bash-completion/completions/losetup \
               ${datadir}/bash-completion/completions/swapon \
               "

FILES_uuid-dev = " \
		${includedir}/uuid \
		${libdir}/libuuid.so \
		${libdir}/pkgconfig/uuid.pc"

FILES_uuid-runtime = "${bindir}/uuidgen ${sbindir}/uuidd \
                      ${sysconfdir}/init.d/uuidd \
                      ${datadir}/bash-completion/completions/uuidd \
                      ${datadir}/bash-completion/completions/uuidgen \
"

FILES_${PN} += "${datadir}/bash-completion/ ${exec_prefix}/sbin/*"

ALTERNATIVE_PRIORITY="100"
ALTERNATIVE_${PN} = "dmesg more blkid blockdev fdisk findfs fsck fsck.minix \
		     fstrim getty hwclock mkfs.minix mkswap pivot_root \
		     switch_root "
ALTERNATIVE_LINK_NAME[dmesg] = "${base_bindir}/dmesg"
ALTERNATIVE_LINK_NAME[more] = "${base_bindir}/more"
ALTERNATIVE_LINK_NAME[blkid] = "${base_sbindir}/blkid"
ALTERNATIVE_LINK_NAME[blockdev] = "${base_sbindir}/blockdev"
ALTERNATIVE_LINK_NAME[fdisk] = "${base_sbindir}/fdisk"
ALTERNATIVE_LINK_NAME[findfs] = "${base_sbindir}/findfs"
ALTERNATIVE_LINK_NAME[fsck] = "${base_sbindir}/fsck"
ALTERNATIVE_LINK_NAME[fsck.minix] = "${base_sbindir}/fsck.minix"
ALTERNATIVE_LINK_NAME[fstrim] = "${base_sbindir}/fstrim"
ALTERNATIVE_LINK_NAME[getty] = "${base_sbindir}/getty"
ALTERNATIVE_TARGET[getty] = "${base_sbindir}/getty.${DPN}"
ALTERNATIVE_LINK_NAME[hwclock] = "${base_sbindir}/hwclock"
ALTERNATIVE_LINK_NAME[mkfs.minix] = "${base_sbindir}/mkfs.minix"
ALTERNATIVE_LINK_NAME[mkswap] = "${base_sbindir}/mkswap"
ALTERNATIVE_LINK_NAME[pivot_root] = "${base_sbindir}/pivot_root"
ALTERNATIVE_LINK_NAME[switch_root] = "${base_sbindir}/switch_root"

ALTERNATIVE_mount = "mount"
ALTERNATIVE_LINK_NAME[mount] = "${base_bindir}/mount"

SYSTEMD_SERVICE_${PN}-uuidd = "uuidd.socket uuidd.service"
SYSTEMD_AUTO_ENABLE_${PN}-uuidd = "disable"
SYSTEMD_SERVICE_${PN}-fstrim = "fstrim.timer fstrim.service"
SYSTEMD_AUTO_ENABLE_${PN}-fstrim = "disable"

BBCLASSEXTEND = "native nativesdk"
