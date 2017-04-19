SUMMARY = "disk encryption support - startup scripts"
DESCRIPTION = "\
	Cryptsetup provides an interface for configuring encryption on block      \
	devices (such as /home or swap partitions), using the Linux kernel        \
	device mapper target dm-crypt. It features integrated Linux Unified Key   \ 
	Setup (LUKS) support. \
	Cryptsetup is backwards compatible with the on-disk format of cryptoloop, \
	but also supports more secure formats. This package includes support for  \
	automatically configuring encrypted devices at boot time via the config   \
	file /etc/crypttab. Additional features are cryptoroot support through    \
	initramfs-tools and several supported ways to read a passphrase or key.   \
"
HOMEPAGE = "http://code.google.com/p/cryptsetup/"
PR = "r2"
inherit debian-package
PV = "1.6.6"

LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=32107dd283b1dfeb66c9b3e6be312326\
	file://COPYING.LGPL;md5=1960515788100ce5f9c98ea78a65dc52"
inherit autotools-brokensep pkgconfig gettext binconfig lib_package

#inherit autotools-brokensep
DEPENDS = "util-linux lvm2 popt libgcrypt chrpath-native"

#disable-selinux: Don't use selinux support
EXTRA_OECONF += "--enable-shared \
		--libdir=${base_libdir} \
		--sbindir=${base_sbindir} \
		--enable-cryptsetup-reencrypt \
		--disable-selinux"

#install follow Debian jessie
do_install_append() {
	install -d ${D}${base_libdir}/cryptsetup/checks
	install -d ${D}${base_libdir}/cryptsetup/scripts
	install -d ${D}${sbindir}
	install -d ${D}${libdir}/pkgconfig
	install -d ${D}${datadir}/initramfs-tools/hooks
	install -d ${D}${datadir}/initramfs-tools/scripts/local-bottom
	install -d ${D}${datadir}/initramfs-tools/scripts/local-top
	install -d ${D}${datadir}/initramfs-tools/conf-hooks.d
	install -d ${D}${datadir}/initramfs-tools/scripts/local-block
	install -d ${D}${datadir}/bug
	install -d ${D}${sysconfdir}/bash_completion.d
	install -d ${D}${sysconfdir}/default
	install -d ${D}${sysconfdir}/init
	install -d ${D}${sysconfdir}/init.d

	#follow debian/rules
	oe_runmake -C ${S}/po install-data-yes DESTDIR=${D}
	oe_runmake -C ${S}/debian/scripts/po install DESTDIR=${D}
	install -m 0644 ${S}/debian/cryptdisks.functions 			\
		${D}${base_libdir}/cryptsetup/
	install -m 0755 ${S}/debian/checks/* 					\
		${D}${base_libdir}/cryptsetup/checks/
	install -m 0755 ${S}/debian/scripts/luksformat 				\
		${D}${sbindir}/
	install -m 0755 ${S}/debian/scripts/cryptdisks_start 			\
		${D}${base_sbindir}/
	install -m 0755 ${S}/debian/scripts/cryptdisks_stop 			\
		${D}${base_sbindir}
	ln -s ../..${base_sbindir}/cryptdisks_start 				\
		${D}${sbindir}/cryptdisks_start
	ln -s ../..${base_sbindir}/cryptdisks_stop 				\
		${D}${sbindir}/cryptdisks_stop
	install -m 0755 ${S}/debian/scripts/decrypt_* 				\
		${D}${base_libdir}/cryptsetup/scripts/
	for i in  ${S}/debian/initramfs/*-hook; do
		install -m 0755 ${i} \
		${D}${datadir}/initramfs-tools/hooks/`basename $i|cut -d- -f1`
	done
	install -m 0755 ${S}/debian/initramfs/cryptopensc-script-local-bottom 	\
		${D}${datadir}/initramfs-tools/scripts/local-bottom/cryptopensc
	install -m 0755 ${S}/debian/initramfs/cryptopensc-script-local-top 	\
		${D}${datadir}/initramfs-tools/scripts/local-top/cryptopensc
	install -m 0644 ${S}/debian/initramfs/cryptroot-conf 			\
		${D}${datadir}/initramfs-tools/conf-hooks.d/cryptsetup
	install -m 0755 ${S}/debian/initramfs/cryptroot-script 			\
		${D}${datadir}/initramfs-tools/scripts/local-top/cryptroot
	install -m 0755 ${S}/debian/initramfs/cryptroot-script-block 		\
		${D}${datadir}/initramfs-tools/scripts/local-block/cryptroot
	install -m 0644 ${S}/debian/cryptdisks.bash_completion 			\
		${D}${sysconfdir}/bash_completion.d/cryptdisks
	install -m 0755 ${S}/debian/cryptsetup.reportbug-script 		\
		${D}${datadir}/bug/cryptsetup
	install -m 0644 ${S}/debian/cryptdisks.default 				\
		${D}${sysconfdir}/default/cryptdisks
	install -m 0644 ${S}/debian/cryptdisks.upstart 				\
		${D}${sysconfdir}/init/cryptdisks.conf
	install -m 0644 ${S}/debian/cryptdisks-udev.upstart 			\
		${D}${sysconfdir}/init/cryptdisks-udev.conf
	install -m 0755 ${S}/debian/cryptdisks.init 				\
		${D}${sysconfdir}/init.d/cryptdisks
	install -m 0755 ${S}/debian/cryptdisks-early.init 			\
		${D}${sysconfdir}/init.d/cryptdisks-early
	rm ${D}${base_libdir}/libcryptsetup.la

	LINKLIB=$(basename $(readlink ${D}${base_libdir}/libcryptsetup.so))
	rm ${D}${base_libdir}/libcryptsetup.so
	ln -s ../..${base_libdir}/$LINKLIB ${D}${libdir}/libcryptsetup.so
	rm -r ${D}${base_libdir}/pkgconfig
	cp -a ${S}/lib/libcryptsetup.pc ${D}${libdir}/pkgconfig/

	${CC} $CFLAGS $LDFLAGS -pedantic -std=c99 				\
	${S}/debian/askpass.c -o ${S}/debian/askpass
	${CC} $CFLAGS $LDFLAGS -pedantic -std=c99 				\
	${S}/debian/passdev.c -o ${S}/debian/scripts/passdev 
	install -m 0755 ${S}/debian/scripts/passdev 				\
		${D}${base_libdir}/cryptsetup/scripts/
	install -m 0755 ${S}/debian/askpass ${D}${base_libdir}/cryptsetup/
	
	chrpath -d ${D}${base_sbindir}/veritysetup
	chrpath -d ${D}${base_sbindir}/cryptsetup
	chrpath -d ${D}${base_sbindir}/cryptsetup-reencrypt
}
PACKAGES =+ "lib${PN}"
PKG_${PN}-dev = "lib${PN}-dev"

FILES_${PN}-bin = "\
	${base_sbindir}/cryptsetup ${base_sbindir}/cryptsetup-reencrypt 	\
	${base_sbindir}/veritysetup ${sbindir}/luksformat ${datadir}/locale/*	\
	"
FILES_lib${PN} = "${base_libdir}/libcryptsetup.so.*"

FILES_${PN}-dbg += "\
	${base_libdir}/cryptsetup/scripts/.debug/* 				\
	${base_libdir}/cryptsetup/.debug/*					\
	"
FILES_${PN} += "${datadir}/bug ${datadir}/initramfs-tools 			\
	${base_libdir}/cryptsetup/*						\
	"
#follow debian/control
RDEPENDS_${PN} += "dmsetup cryptsetup-bin"
RREPLACES_${PN} += "hashalot"
RREPLACES_${PN}-bin += "${PN}"
RDEPENDS_lib${PN} += "libgpg-error libgcrypt"
RDEPENDS_lib${PN}-dev += "lib${PN}4"
