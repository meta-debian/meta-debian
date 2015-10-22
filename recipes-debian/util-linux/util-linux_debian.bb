#
# Base recipe: meta/recipes-core/util-linux/util-linux_2.24.1.bb
# Base branch: daisy
# Base commit: 828b6754c205fe6e5cd5f44d1ff50da304b3273d
#

SUMMARY = "A suite of basic system administration utilities"
DESCRIPTION = "Util-linux includes a suite of basic system administration utilities \
commonly found on most Linux systems.  Some of the more important utilities include \
disk partitioning, kernel message management, filesystem creation, and system login."

PR = "r0"

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
DEPENDS = "zlib ncurses"
DEPENDS_append_class-native = " lzo-native"
DEPENDS_append_class-nativesdk = " lzo-native"

# init.d/hwclock.sh require lsb-base
RDEPENDS_${PN}_class-target += "lsb-base"

# Follow Debian/rules
# FIXME: Temporary remove configs: --with-slang --with-systemd --with-selinux
# and add configs: --disable-use-tty-group for building successfully.
EXTRA_OECONF = "--enable-line --libdir=${base_libdir} \
		--libexecdir=${libdir} --localstatedir=/run \
		--disable-silent-rules --without-python --disable-login \
		--disable-nologin --disable-su --disable-sulogin \
		--disable-last --disable-mesg --disable-mountpoint \
		--disable-kill --disable-eject --disable-chfn-chsh \
		--enable-raw --enable-partx --enable-tunelp\
		--sbindir=${base_sbindir} --disable-use-tty-group"

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
}

PACKAGES = "bsdutils libblkid-dev libblkid1 libmount-dev libmount1 \
	    libsmartcols-dev libsmartcols1 libuuid1 mount uuid-runtime uuid-dev \
	    libmount-staticdev libsmartcols-staticdev uuid-staticdev \
	    libblkid-staticdev ${PN}-dbg ${PN} ${PN}-locales ${PN}-doc"

FILES_bsdutils = " \
		${bindir}/logger ${bindir}/renice ${bindir}/script \
		${bindir}/scriptreplay ${bindir}/wall ${bindir}/`\
		${datadir}/bash-completion/completions/logger \
		${datadir}/bash-completion/completions/script \
		${datadir}/bash-completion/completions/scriptreplay \
		${datadir}/bash-completion/completions/wall \
		${datadir}/man/man1/logger.1 \
		${datadir}/man/man1/renice.1 \
		${datadir}/man/man1/script.1 \
		${datadir}/man/man1/scriptreplay.1 \
		${datadir}/man/man1/wall.1"

FILES_libblkid1 = " \
		${base_libdir}/libblkid.so.*"
FILES_libblkid-dev = " \
		${includedir}/blkid/* \
		${libdir}/libblkid.so \
		${libdir}/pkgconfig/blkid.pc \
		${datadir}/man/man3/libblkid.3"
FILES_libblkid-staticdev = "${libdir}/libblkid.a"

FILES_libmount1 = " \
		${base_libdir}/libmount.so*"

FILES_libmount-dev = " \
		${includedir}/libmount/libmount.h \
		${libdir}/libmount.so \
		${libdir}/pkgconfig/mount.pc"
FILES_libmount-staticdev = " \
		${libdir}/libmount.a"

FILES_libsmartcols1 = " \
		${base_libdir}/libsmartcols.so*"
FILES_libsmartcols-dev = " \
		${includedir}/libsmartcols/libsmartcols.h \
		${libdir}/libsmartcols.so \
		${libdir}/pkgconfig/smartcols.pc"
FILES_libsmartcols-staticdev = " \
		${libdir}/libsmartcols.a"
FILES_libuuid1 = " \
		${base_libdir}/libuuid.so.*"
FILES_mount = " \
		${base_bindir}/mount \
		${base_bindir}/findmnt \
		${base_bindir}/umount \
		${base_sbindir}/losetup \
		${base_sbindir}/swapoff \
		${base_sbindir}/swapon \
		${datadir}/bash-completion/completions/findmnt \
		${datadir}/bash-completion/completions/losetup \
		${datadir}/bash-completion/completions/swapon \
		${datadir}/man/man5/fstab.5 \
		${datadir}/man/man8/findmnt.8 \
		${datadir}/man/man8/losetup.8 \
		${datadir}/man/man8/mount.8 \
		${datadir}/man/man8/swapoff.8 \
		${datadir}/man/man8/swapon.8 \
		${datadir}/man/man8/umount.8"	
		
FILES_${PN} = " \
		${sysconfdir} ${base_bindir}/* ${base_sbindir}/* \
		${sbindir}/* ${bindir}/* ${datadir}/bash-completion/completions"
FILES_${PN}-doc = " \
                ${datadir}/doc ${datadir}/man"

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
FILES_uuid-runtime = " \
		${bindir}/uuid* \
		${sbindir}/uuidd \
		${datadir}/bash-completion/completions/uuid* \
		${datadir}/man/man8/uuidd.8 \
		${datadir}/man/man1/uuidgen.1 \
		"
BBCLASSEXTEND = "native nativesdk"
