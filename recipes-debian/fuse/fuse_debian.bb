SUMMARY = "Filesystem in Userspace"
DESCRIPTION = "\
	Filesystem in Userspace (FUSE) is a simple interface for userspace programs to \
	export a virtual filesystem to the Linux kernel. It also aims to provide a \
	secure method for non privileged users to create and mount their own filesystem \
	implementations\
"
HOMEPAGE = "http://fuse.sourceforge.net/"

PR = "r1"
inherit debian-package
PV = "2.9.3"

LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = "\
		file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
		file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c"

inherit autotools-brokensep

do_configure() {
        aclocal
        autoconf
        oe_runconf
}

#install follow debian jessie
do_install_append() {
	install -d ${D}${base_bindir}
	install -d ${D}${base_libdir}/udev/rules.d
	install -d ${D}${base_libdir}/modules-load.d
	install -d ${D}${datadir}/initramfs-tools/hooks
	
	install -m 0755 ${S}/util/fusermount ${D}${base_bindir}
	install -m 0755 ${S}/util/ulockmgr_server ${D}${base_bindir}
	
	install -m 0644 ${S}/debian/fuse.udev \
		${D}${base_libdir}/udev/rules.d/60-fuse.rules
	install -m 0644 ${S}/debian/local/fuse.kmod \
		${D}${base_libdir}/modules-load.d/fuse.conf
	install -m 0644 ${S}/debian/local/fuse.conf ${D}${sysconfdir}/fuse.conf
	
	install -m 0644 ${S}/debian/local/fuse.hook \
		${D}${datadir}/initramfs-tools/hooks/fuse

	#remove the unwanted files
	rm -r ${D}${bindir}
	rm -r ${D}/dev
	rm ${D}${libdir}/*.la
	rm ${D}${sysconfdir}/init.d/fuse
	rm ${D}${sysconfdir}/udev/rules.d/99-fuse.rules

	mv ${D}${libdir}/libfuse.so.* ${D}${base_libdir}/
	mv ${D}${libdir}/libulockmgr.so.* ${D}${base_libdir}/

	LINKLIB=$(basename $(readlink ${D}${libdir}/libfuse.so))
	rm ${D}${libdir}/libfuse.so
	ln -s ../..${base_libdir}/$LINKLIB ${D}${libdir}/libfuse.so

	LINKLIB=$(basename $(readlink ${D}${libdir}/libulockmgr.so))
	rm ${D}${libdir}/libulockmgr.so
	ln -s ../..${base_libdir}/$LINKLIB ${D}${libdir}/libulockmgr.so	
}

PACKAGES =+ "lib${PN}"

PKG_${PN}-dev = "lib${PN}-dev"
PKG_lib${PN} = "lib${PN}2"

FILES_lib${PN} = "${base_libdir}/*.so.*"
FILES_${PN} += "\
		${datadir}/initramfs-tools/hooks/fuse \
		${base_libdir}/modules-load.d/fuse.conf"
