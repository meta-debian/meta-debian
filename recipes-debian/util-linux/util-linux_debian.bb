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
	install ${S}/debian/util-linux.runuser.pam ${D}${sysconfdir}/runuser
	install ${S}/debian/util-linux.runuser-l.pam ${D}${sysconfdir}/runuser-l

	# install /etc/default/hwclock
	install -d ${D}${sysconfdir}/default
	install ${S}/debian/util-linux.hwclock.default ${D}${sysconfdir}/hwclock
}

PACKAGES = "bsdutils libblkid-dev libblkid1 libmount-dev libmount1 \
	    libsmartcols-dev libsmartcols1 libuuid1 mount ${PN} ${PN}-locales \
	    uuid-dev uuid-runtime ${PN}-dbg libblkid-staticdev libmount-staticdev \
	    libsmartcols-staticdev uuid-staticdev"

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
		${sysconfdir} \
		${base_bindir}/dmesg \
		${base_bindir}/lsblk \
		${base_bindir}/more \
		${base_bindir}/tailf \
		${base_bindir}/wdctl \
		${base_sbindir}/agetty \
		${base_sbindir}/blkdiscard \
		${base_sbindir}/blkid \
		${base_sbindir}/blockdev \
		${base_sbindir}/cfdisk \
		${base_sbindir}/chcpu \
		${base_sbindir}/ctrlaltdel \
		${base_sbindir}/fdisk \
		${base_sbindir}/findfs \
		${base_sbindir}/fsck \
		${base_sbindir}/fsck.cramfs \
		${base_sbindir}/fsck.minix \
		${base_sbindir}/fsfreeze \
		${base_sbindir}/fstrim \
		${base_sbindir}/getty \
		${base_sbindir}/hwclock \
		${base_sbindir}/isosize \
		${base_sbindir}/mkfs \
		${base_sbindir}/mkfs.bfs \
		${base_sbindir}/mkfs.cramfs \
		${base_sbindir}/mkfs.minix \
		${base_sbindir}/mkswap \
		${base_sbindir}/pivot_root \
		${base_sbindir}/raw \
		${base_sbindir}/runuser \
		${base_sbindir}/sfdisk \
		${base_sbindir}/swaplabel \
		${base_sbindir}/switch_root \
		${base_sbindir}/wipefs \
		${sbindir}/fdformat \
		${sbindir}/ldattach \
		${sbindir}/readprofile \
		${sbindir}/rtcwake \
		${sbindir}/tunelp \
		${bindir}/addpart \
		${bindir}/chrt \
		${bindir}/delpart \
		${bindir}/fallocate \
		${bindir}/flock \
		${bindir}/getopt \
		${bindir}/i386 \
		${bindir}/ionice \
		${bindir}/ipcmk \
		${bindir}/ipcrm \
		${bindir}/ipcs \
		${bindir}/line \
		${bindir}/linux32 \
		${bindir}/linux64 \
		${bindir}/lscpu \
		${bindir}/lslocks \
		${bindir}/mcookie \
		${bindir}/namei \
		${bindir}/nsenter \
		${bindir}/partx \
		${bindir}/pg \
		${bindir}/prlimit \
		${bindir}/rename \
		${bindir}/resizepart \
		${bindir}/rev \
		${bindir}/setarch \
		${bindir}/setsid \
		${bindir}/setterm \
		${bindir}/taskset \
		${bindir}/unshare \
		${bindir}/utmpdump \
		${bindir}/whereis \
		${bindir}/lslogins \
		${bindir}/isosize \
		${bindir}/uname26 \
		${datadir}/doc \
		${datadir}/man/man1/chrt.1 \
		${datadir}/man/man1/dmesg.1 \
		${datadir}/man/man1/fallocate.1 \
		${datadir}/man/man1/flock.1 \
		${datadir}/man/man1/getopt.1 \
		${datadir}/man/man1/ionice.1 \
		${datadir}/man/man1/ipcmk.1 \
		${datadir}/man/man1/ipcrm.1 \
		${datadir}/man/man1/ipcs.1 \
		${datadir}/man/man1/line.1 \
		${datadir}/man/man1/linux32.1 \
		${datadir}/man/man1/linux64.1 \
		${datadir}/man/man1/ls*.1 \
		${datadir}/man/man1/m*.1 \
		${datadir}/man/man1/n*.1 \
		${datadir}/man/man1/p*.1 \
		${datadir}/man/man1/rename*.1 \
		${datadir}/man/man1/rev.1 \
		${datadir}/man/man1/runuser.1 \
		${datadir}/man/man1/setsid.1 \
		${datadir}/man/man1/setterm.1 \
		${datadir}/man/man1/tailf.1 \
		${datadir}/man/man1/taskset.1 \
		${datadir}/man/man1/u*.1 \
		${datadir}/man/man1/w*.1 \
		${datadir}/man/man5/hwclock.5 \
		${datadir}/man/man5/terminal-colors.d.5 \
		${datadir}/man/man8/fsfreeze.8 \
		${datadir}/man/man8/readprofile.8 \
		${datadir}/man/man8/ctrlaltdel.8 \
		${datadir}/man/man8/delpart.8 \
		${datadir}/man/man8/blkid.8 \
		${datadir}/man/man8/uuidd.8 \
		${datadir}/man/man8/wipefs.8 \
		${datadir}/man/man8/mkfs.cramfs.8 \
		${datadir}/man/man8/chcpu.8 \
		${datadir}/man/man8/linux64.8 \
		${datadir}/man/man8/findfs.8 \
		${datadir}/man/man8/cfdisk.8 \
		${datadir}/man/man8/agetty.8 \
		${datadir}/man/man8/mkfs.minix.8 \
		${datadir}/man/man8/addpart.8 \
		${datadir}/man/man8/fdisk.8 \
		${datadir}/man/man8/fsck.minix.8 \
		${datadir}/man/man8/fdformat.8 \
		${datadir}/man/man8/mkfs.bfs.8 \
		${datadir}/man/man8/lslocks.8 \
		${datadir}/man/man8/fstrim.8 \
		${datadir}/man/man8/fsck.cramfs.8 \
		${datadir}/man/man8/rtcwake.8 \
		${datadir}/man/man8/blockdev.8 \
		${datadir}/man/man8/lsblk.8 \
		${datadir}/man/man8/resizepart.8 \
		${datadir}/man/man8/i386.8 \
		${datadir}/man/man8/mkswap.8 \
		${datadir}/man/man8/hwclock.8 \
		${datadir}/man/man8/fsck.8 \
		${datadir}/man/man8/uname26.8 \
		${datadir}/man/man8/mkfs.8 \
		${datadir}/man/man8/pivot_root.8 \
		${datadir}/man/man8/setarch.8 \
		${datadir}/man/man8/sfdisk.8 \
		${datadir}/man/man8/partx.8 \
		${datadir}/man/man8/ldattach.8 \
		${datadir}/man/man8/switch_root.8 \
		${datadir}/man/man8/wdctl.8 \
		${datadir}/man/man8/blkdiscard.8 \
		${datadir}/man/man8/isosize.8 \
		${datadir}/man/man8/linux32.8 \
		${datadir}/man/man8/tunelp.8 \
		${datadir}/man/man8/swaplabel.8 \
		${datadir}/man/man8/raw.8 \
		${datadir}/bash-completion/completions/sfdisk \                                 
		${datadir}/bash-completion/completions/pivot_root \                             
		${datadir}/bash-completion/completions/more \                                   
		${datadir}/bash-completion/completions/mkswap \                                 
		${datadir}/bash-completion/completions/delpart \                                
		${datadir}/bash-completion/completions/blkid \                                  
		${datadir}/bash-completion/completions/fsfreeze \                               
		${datadir}/bash-completion/completions/colrm \                                  
		${datadir}/bash-completion/completions/hexdump \                                
		${datadir}/bash-completion/completions/ionice \                                 
		${datadir}/bash-completion/completions/fallocate \                              
		${datadir}/bash-completion/completions/unshare \                                
		${datadir}/bash-completion/completions/mcookie \                                
		${datadir}/bash-completion/completions/utmpdump \                               
		${datadir}/bash-completion/completions/nsenter \                                
		${datadir}/bash-completion/completions/setarch \                                
		${datadir}/bash-completion/completions/getopt \                                 
		${datadir}/bash-completion/completions/lscpu \                                  
		${datadir}/bash-completion/completions/lsblk \                                  
		${datadir}/bash-completion/completions/mkfs.cramfs \                            
		${datadir}/bash-completion/completions/setterm \                                
		${datadir}/bash-completion/completions/col \                                    
		${datadir}/bash-completion/completions/readprofile \                            
		${datadir}/bash-completion/completions/namei \                                  
		${datadir}/bash-completion/completions/rev \                                    
		${datadir}/bash-completion/completions/pg \                                     
		${datadir}/bash-completion/completions/ldattach \                               
		${datadir}/bash-completion/completions/wdctl \                                  
		${datadir}/bash-completion/completions/mkfs \                                   
		${datadir}/bash-completion/completions/partx \                                  
		${datadir}/bash-completion/completions/cfdisk \                                 
		${datadir}/bash-completion/completions/fstrim \                                 
		${datadir}/bash-completion/completions/swaplabel \                              
		${datadir}/bash-completion/completions/mkfs.bfs \                               
		${datadir}/bash-completion/completions/blkdiscard \                             
		${datadir}/bash-completion/completions/hwclock \                                
		${datadir}/bash-completion/completions/tailf \                                  
		${datadir}/bash-completion/completions/setsid \                                 
		${datadir}/bash-completion/completions/blockdev \                               
		${datadir}/bash-completion/completions/tunelp \                                 
		${datadir}/bash-completion/completions/colcrt \                                 
		${datadir}/bash-completion/completions/uuidd \                                  
		${datadir}/bash-completion/completions/fdformat \                               
		${datadir}/bash-completion/completions/rtcwake \                                
		${datadir}/bash-completion/completions/look \                                   
		${datadir}/bash-completion/completions/ipcrm \                                  
		${datadir}/bash-completion/completions/lslocks \                                
		${datadir}/bash-completion/completions/fdisk \
		${datadir}/bash-completion/completions/ctrlaltdel \                             
		${datadir}/bash-completion/completions/resizepart \                             
		${datadir}/bash-completion/completions/chcpu \ 
		${datadir}/bash-completion/completions/fsck.minix \                             
		${datadir}/bash-completion/completions/fsck \                                   
		${datadir}/bash-completion/completions/chrt \                                   
		${datadir}/bash-completion/completions/raw \                                    
		${datadir}/bash-completion/completions/prlimit \                                
		${datadir}/bash-completion/completions/ipcs \                                   
		${datadir}/bash-completion/completions/isosize \                                
		${datadir}/bash-completion/completions/taskset \                                
		${datadir}/bash-completion/completions/whereis \                                
		${datadir}/bash-completion/completions/cal \                                    
		${datadir}/bash-completion/completions/column \                                 
		${datadir}/bash-completion/completions/wipefs \                                 
		${datadir}/bash-completion/completions/mkfs.minix \                             
		${datadir}/bash-completion/completions/ul \                                     
		${datadir}/bash-completion/completions/fsck.cramfs \                            
		${datadir}/bash-completion/completions/rename \                                 
		${datadir}/bash-completion/completions/flock \                                  
		${datadir}/bash-completion/completions/addpart \
		"
FILES_${PN}-dbg += "${prefix}/src/* \
		${bindir}/.debug/* \
		${base_sbindir}/.debug/*"

BBCLASSEXTEND = "native nativesdk"
