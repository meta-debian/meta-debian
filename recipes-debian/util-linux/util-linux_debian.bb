#
# Base recipe: meta/recipes-core/util-linux/util-linux_2.24.1.bb
# Base branch: daisy
# Base commit: 828b6754c205fe6e5cd5f44d1ff50da304b3273d
#

SUMMARY = "A suite of basic system administration utilities"
DESCRIPTION = "Util-linux includes a suite of basic system administration utilities \
commonly found on most Linux systems.  Some of the more important utilities include \
disk partitioning, kernel message management, filesystem creation, and system login."

PR = "r2"

inherit debian-package

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
EXTRA_OECONF = "--enable-line --libdir=${base_libdir} \
		--libexecdir=${libdir} --localstatedir=/run \
		--disable-silent-rules --disable-login \
		--disable-nologin --disable-sulogin \
		--disable-last --disable-mesg --disable-mountpoint \
		--disable-kill --disable-eject --disable-chfn-chsh \
		--enable-raw --enable-partx --enable-tunelp\
		--sbindir=${base_sbindir} --disable-use-tty-group"
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
	mv ${D}${sbindir}/addpart ${D}${bindir}
	mv ${D}${sbindir}/resizepart ${D}${bindir}
	mv ${D}${sbindir}/partx ${D}${bindir}
	mv ${D}${sbindir}/delpart ${D}${bindir}
	
	install -d ${D}${sysconfdir}/init.d
	install ${S}/debian/util-linux.hwclock.sh.init \
					${D}${sysconfdir}/init.d/hwclock.sh

	# install pam
	install -d ${D}${sysconfdir}/pam.d
	install ${S}/debian/util-linux.runuser.pam ${D}${sysconfdir}/pam.d/runuser
	install ${S}/debian/util-linux.runuser-l.pam ${D}${sysconfdir}/pam.d/runuser-l

	# install /etc/default/hwclock
	install -d ${D}${sysconfdir}/default
	install ${S}/debian/util-linux.hwclock.default ${D}${sysconfdir}/default/hwclock

	# create soft link getty to agetty command
	ln -sf agetty ${D}/${base_sbindir}/getty.${DPN}

	# Install files belong to util-linux package follow Debian
	install -m 0755 ${D}${bindir}/isosize ${D}${base_sbindir}
	# perl gets to do rename, not us.
	mv ${D}${bindir}/rename ${D}${bindir}/rename.ul
}

PACKAGES =+ " \
	mount bsdutils libblkid-dev libblkid1 libmount-dev libmount1 \
	libsmartcols-dev libsmartcols1 libuuid1 uuid-runtime uuid-dev \
	libmount-staticdev libsmartcols-staticdev uuid-staticdev \
	libblkid-staticdev ${PN}-bash-completion \
	${PN}-agetty ${PN}-blkdiscard ${PN}-blkid ${PN}-blockdev \
	${PN}-cfdisk ${PN}-chcpu ${PN}-ctrlaltdel ${PN}-fdisk \
	${PN}-findfs ${PN}-fsck.cramfs ${PN}-fsck ${PN}-fsfreeze \
	${PN}-fstrim ${PN}-hwclock ${PN}-isosize ${PN}-losetup \
	${PN}-mkfs.cramfs ${PN}-mkfs ${PN}-mkswap ${PN}-pivot-root \
	${PN}-raw ${PN}-runuser ${PN}-sfdisk ${PN}-swaplabel \
	${PN}-swaponoff ${PN}-switch-root ${PN}-wipefs \
	${PN}-fdformat ${PN}-ldattach ${PN}-readprofile \
	${PN}-rtcwake ${PN}-tunelp ${PN}-uuidd \
	${PN}-addpart ${PN}-chrt ${PN}-delpart ${PN}-fallocate ${PN}-flock \
	${PN}-getopt ${PN}-ionice ${PN}-ipcmk ${PN}-ipcrm ${PN}-ipcs \
	${PN}-line ${PN}-logger ${PN}-lscpu ${PN}-lslocks ${PN}-lslogins \
	${PN}-mcookie ${PN}-namei ${PN}-nsenter ${PN}-pg ${PN}-partx \
	${PN}-prlimit ${PN}-rename.ul ${PN}-renice ${PN}-resizepart \
	${PN}-rev ${PN}-script ${PN}-scriptreplay ${PN}-setsid ${PN}-setterm \
	${PN}-su ${PN}-taskset ${PN}-unshare ${PN}-utmpdump \
	${PN}-whereis ${PN}-setarch \
	${PN}-dmesg ${PN}-lsblk ${PN}-findmnt ${PN}-more \
	${PN}-tailf ${PN}-mount ${PN}-wdctl \
	"

FILES_${PN}-agetty = "${base_sbindir}/*getty*"
FILES_${PN}-blkdiscard = "${base_sbindir}/blkdiscard"
FILES_${PN}-blkid = "${base_sbindir}/blkid*"
FILES_${PN}-blockdev = "${base_sbindir}/blockdev*"
FILES_${PN}-cfdisk = "${base_sbindir}/cfdisk"
FILES_${PN}-chcpu = "${base_sbindir}/chcpu"
FILES_${PN}-ctrlaltdel = "${base_sbindir}/ctrlaltdel"
FILES_${PN}-fdisk = "${base_sbindir}/fdisk*"
FILES_${PN}-findfs = "${base_sbindir}/findfs*"
FILES_${PN}-fsck.cramfs = "${base_sbindir}/fsck.cramfs"
FILES_${PN}-fsck = "${base_sbindir}/fsck*"
FILES_${PN}-fsfreeze = "${base_sbindir}/fsfreeze"
FILES_${PN}-fstrim = "${base_sbindir}/fstrim*"
FILES_${PN}-hwclock = "${base_sbindir}/hwclock*"
FILES_${PN}-isosize = "${base_sbindir}/isosize"
FILES_${PN}-losetup = "${base_sbindir}/losetup"
FILES_${PN}-mkfs.cramfs = "${base_sbindir}/mkfs.cramfs"
FILES_${PN}-mkfs = "${base_sbindir}/mkfs*"
FILES_${PN}-mkswap = "${base_sbindir}/mkswap*"
FILES_${PN}-pivot-root = "${base_sbindir}/pivot_root*"
FILES_${PN}-raw = "${base_sbindir}/raw"
FILES_${PN}-runuser = "${base_sbindir}/runuser"
FILES_${PN}-sfdisk = "${base_sbindir}/sfdisk"
FILES_${PN}-swaplabel = "${base_sbindir}/swaplabel"
FILES_${PN}-swaponoff = "${base_sbindir}/swapon ${base_sbindir}/swapoff"
FILES_${PN}-switch-root = "${base_sbindir}/switch_root*"
FILES_${PN}-wipefs = "${base_sbindir}/wipefs"

FILES_${PN}-fdformat = "${sbindir}/fdformat"
FILES_${PN}-ldattach = "${sbindir}/ldattach"
FILES_${PN}-readprofile = "${sbindir}/readprofile"
FILES_${PN}-rtcwake = "${sbindir}/rtcwake"
FILES_${PN}-tunelp = "${sbindir}/tunelp"
FILES_${PN}-uuidd = "${sbindir}/uuidd"

FILES_${PN}-addpart = "${bindir}/addpart"
FILES_${PN}-chrt = "${bindir}/chrt"
FILES_${PN}-delpart = "${bindir}/delpart"
FILES_${PN}-fallocate = "${bindir}/fallocate"
FILES_${PN}-flock = "${bindir}/flock"
FILES_${PN}-getopt = "${bindir}/getopt"
FILES_${PN}-ionice = "${bindir}/ionice"
FILES_${PN}-ipcmk = "${bindir}/ipcmk"
FILES_${PN}-ipcrm = "${bindir}/ipcrm"
FILES_${PN}-ipcs = "${bindir}/ipcs"
FILES_${PN}-line = "${bindir}/line"
FILES_${PN}-logger = "${bindir}/logger"
FILES_${PN}-lscpu = "${bindir}/lscpu"
FILES_${PN}-lslocks = "${bindir}/lslocks"
FILES_${PN}-lslogins = "${bindir}/lslogins"
FILES_${PN}-mcookie = "${bindir}/mcookie"
FILES_${PN}-namei = "${bindir}/namei"
FILES_${PN}-nsenter = "${bindir}/nsenter"
FILES_${PN}-partx = "${bindir}/partx"
FILES_${PN}-pg = "${bindir}/pg"
FILES_${PN}-prlimit = "${bindir}/prlimit"
FILES_${PN}-rename.ul = "${bindir}/rename.ul"
FILES_${PN}-renice = "${bindir}/renice"
FILES_${PN}-resizepart = "${bindir}/resizepart"
FILES_${PN}-rev = "${bindir}/rev"
FILES_${PN}-script = "${bindir}/script"
FILES_${PN}-scriptreplay = "${bindir}/scriptreplay"
FILES_${PN}-setsid = "${bindir}/setsid"
FILES_${PN}-setterm = "${bindir}/setterm"
FILES_${PN}-su = "${bindir}/su"
FILES_${PN}-taskset = "${bindir}/taskset"
FILES_${PN}-unshare = "${bindir}/unshare"
FILES_${PN}-utmpdump = "${bindir}/utmpdump"
FILES_${PN}-whereis = "${bindir}/whereis"
FILES_${PN}-setarch = "${bindir}/*"

FILES_${PN}-dmesg = "${base_bindir}/dmesg*"
FILES_${PN}-findmnt = "${base_bindir}/findmnt"
FILES_${PN}-lsblk = "${base_bindir}/lsblk"
FILES_${PN}-more = "${base_bindir}/more*"
FILES_${PN}-tailf = "${base_bindir}/tailf"
FILES_${PN}-mount = "${base_bindir}/mount*"
FILES_${PN}-wdctl = "${base_bindir}/wdctl"

FILES_${PN}-bash-completion += "${datadir}/bash-completion"

FILES_libblkid1 = "${base_libdir}/libblkid.so.*"
FILES_libblkid-dev = " \
		${includedir}/blkid/* \
		${libdir}/libblkid.so \
		${libdir}/pkgconfig/blkid.pc"
FILES_libblkid-staticdev = "${libdir}/libblkid.a"
FILES_libmount1 = "${base_libdir}/libmount.so*"
FILES_libmount-dev = " \
		${includedir}/libmount/libmount.h \
		${libdir}/libmount.so \
		${libdir}/pkgconfig/mount.pc"
FILES_libmount-staticdev = "${libdir}/libmount.a"
FILES_libsmartcols1 = "${base_libdir}/libsmartcols.so*"
FILES_libsmartcols-dev = " \
		${includedir}/libsmartcols/libsmartcols.h \
		${libdir}/libsmartcols.so \
		${libdir}/pkgconfig/smartcols.pc"
FILES_libsmartcols-staticdev = "${libdir}/libsmartcols.a"
FILES_libuuid1 = "${base_libdir}/libuuid.so.*"
FILES_bsdutils += "${bindir}/wall"
RDEPENDS_bsdutils += " \
		${PN}-logger ${PN}-renice  \
		${PN}-script ${PN}-scriptreplay"
FILES_mount += "${base_bindir}/umount"
RDEPENDS_mount += " \
		${PN}-swaponoff ${PN}-mount  \
		${PN}-losetup ${PN}-findmnt "
FILES_${PN} = "${sysconfdir}"
RDEPENDS_${PN}_class-target +=" \
		${PN}-agetty ${PN}-blkdiscard ${PN}-blkid ${PN}-blockdev \
		${PN}-cfdisk ${PN}-chcpu ${PN}-ctrlaltdel ${PN}-fdisk \
		${PN}-findfs ${PN}-fsck.cramfs ${PN}-fsck ${PN}-fsfreeze \
		${PN}-fstrim ${PN}-hwclock ${PN}-isosize ${PN}-mkfs ${PN}-wipefs \
		${PN}-mkfs.cramfs ${PN}-mkswap ${PN}-pivot-root ${PN}-raw \
		${PN}-sfdisk ${PN}-swaplabel ${PN}-switch-root \
		${PN}-dmesg ${PN}-lsblk ${PN}-more ${PN}-tailf ${PN}-wdctl \
		${PN}-fdformat ${PN}-ldattach ${PN}-readprofile \
		${PN}-rtcwake ${PN}-tunelp \
		${PN}-addpart ${PN}-chrt ${PN}-delpart ${PN}-fallocate \
		${PN}-flock ${PN}-getopt ${PN}-ionice ${PN}-ipcmk ${PN}-ipcrm \
		${PN}-ipcs ${PN}-line ${PN}-lscpu ${PN}-lslocks ${PN}-mcookie \
		${PN}-namei ${PN}-nsenter ${PN}-partx ${PN}-pg ${PN}-prlimit \
		${PN}-rename.ul ${PN}-resizepart ${PN}-rev ${PN}-setarch ${PN}-setsid \
		${PN}-setterm ${PN}-taskset ${PN}-unshare ${PN}-utmpdump ${PN}-whereis \
		"
RDEPENDS_${PN}_class-target += "${@bb.utils.contains('DISTRO_FEATURES', 'pam', '${PN}-runuser', '', d)}"
FILES_${PN}-doc = "${datadir}/doc ${datadir}/man"
FILES_${PN}-dbg += "${prefix}/src/* \
		${bindir}/.debug/* \
		${base_sbindir}/.debug/*"
FILES_uuid-dev = " \
		${includedir}/uuid \
		${libdir}/libuuid.so \
		${libdir}/pkgconfig/uuid.pc \
		${datadir}/man/man3/uuid*.3"
FILES_uuid-staticdev = " \
		${libdir}/libuuid.a"
FILES_uuid-runtime += "${bindir}/uuidgen"
RDEPENDS_uuid-runtime = "${PN}-uuidd"

ALTERNATIVE_PRIORITY="100"
ALTERNATIVE_${PN}-dmesg = "dmesg"
ALTERNATIVE_LINK_NAME[dmesg] = "${base_bindir}/dmesg"
ALTERNATIVE_${PN}-more = "more"
ALTERNATIVE_LINK_NAME[more] = "${base_bindir}/more"
ALTERNATIVE_${PN}-blkid = "blkid"
ALTERNATIVE_LINK_NAME[blkid] = "${base_sbindir}/blkid"
ALTERNATIVE_${PN}-blockdev = "blockdev"
ALTERNATIVE_LINK_NAME[blockdev] = "${base_sbindir}/blockdev"
ALTERNATIVE_${PN}-fdisk = "fdisk"
ALTERNATIVE_LINK_NAME[fdisk] = "${base_sbindir}/fdisk"
ALTERNATIVE_${PN}-fsck = "fsck fsck.minix"
ALTERNATIVE_LINK_NAME[fsck] = "${base_sbindir}/fsck"
ALTERNATIVE_LINK_NAME[fsck.minix] = "${base_sbindir}/fsck.minix"
ALTERNATIVE_${PN}-fstrim = "fstrim"
ALTERNATIVE_LINK_NAME[fstrim] = "${base_sbindir}/fstrim"
ALTERNATIVE_${PN}-agetty = "getty"
ALTERNATIVE_LINK_NAME[getty] = "${base_sbindir}/getty"
ALTERNATIVE_TARGET[getty] = "${base_sbindir}/getty.${DPN}"
ALTERNATIVE_${PN}-hwclock = "hwclock"
ALTERNATIVE_LINK_NAME[hwclock] = "${base_sbindir}/hwclock"
ALTERNATIVE_${PN}-mkfs = "mkfs.minix"
ALTERNATIVE_LINK_NAME[mkfs.minix] = "${base_sbindir}/mkfs.minix"
ALTERNATIVE_${PN}-mkswap = "mkswap"
ALTERNATIVE_LINK_NAME[mkswap] = "${base_sbindir}/mkswap"
ALTERNATIVE_${PN}-pivot-root = "pivot_root"
ALTERNATIVE_LINK_NAME[pivot_root] = "${base_sbindir}/pivot_root"
ALTERNATIVE_${PN}-switch-root = "switch_root"
ALTERNATIVE_LINK_NAME[switch_root] = "${base_sbindir}/switch_root"
ALTERNATIVE_${PN}-mount = "mount"
ALTERNATIVE_LINK_NAME[mount] = "${base_bindir}/mount"
ALTERNATIVE_${PN}-findfs = "findfs"
ALTERNATIVE_LINK_NAME[findfs] = "${base_sbindir}/findfs"

SYSTEMD_SERVICE_${PN}-uuidd = "uuidd.socket uuidd.service"
SYSTEMD_AUTO_ENABLE_${PN}-uuidd = "disable"
SYSTEMD_SERVICE_${PN}-fstrim = "fstrim.timer fstrim.service"
SYSTEMD_AUTO_ENABLE_${PN}-fstrim = "disable"

BBCLASSEXTEND = "native nativesdk"
