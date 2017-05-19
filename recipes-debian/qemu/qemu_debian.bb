#
# Base recipe: meta/recipes-devtools/qemu/qemu_2.4.0-rc4.bb
# Base branch: master
#

SUMMARY = "Fast open source processor emulator"
HOMEPAGE = "http://qemu.org"

PR = "r1"

inherit debian-package
PV = "2.1+dfsg"

# qemu-enlarge-env-entry-size.patch:
#   Add addition environment space to boot loader qemu-system-mips
# Qemu-Arm-versatilepb-Add-memory-size-checking_debian.patch
#   Add memory size checking 
# replace-bios-256k-by-128k.patch:
#   bios-256k.bin is not provided by qemu-bios-native	
SRC_URI += "\
file://qemu-enlarge-env-entry-size.patch \
file://Qemu-Arm-versatilepb-Add-memory-size-checking_debian.patch \
file://replace-bios-256k-by-128k.patch \
file://add-ptest-in-makefile.patch \
file://run-ptest \
"

LICENSE = "GPL-2.0 & LGPL-2.1"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=441c28d2cf86e15a37fa47e15a72fbac \
file://COPYING.LIB;md5=79ffa0ec772fa86740948cb7327a0cc7"

DEPENDS = "glib-2.0 zlib pixman dtc \
           ${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'virtual/libx11', '', d)}"
DEPENDS_class-native = "zlib-native glib-2.0-native pixman-native dtc-native"
DEPENDS_class-nativesdk = "nativesdk-zlib nativesdk-glib-2.0 nativesdk-pixman nativesdk-dtc"
RDEPENDS_${PN}_class-target += "bash python"
# qemu-bios-native provides BIOS images for qemu-system
DEPENDS_class-native += "qemu-bios-native"
RDEPENDS_${PN}-ptest = "bash make"

inherit autotools-brokensep ptest

#Since environment doesn't have libsdl, so qemu should not depend
#on it.
#
EXTRA_OECONF += "--disable-blobs --disable-sdl"
EXTRA_OECONF_class-nativesdk += "--disable-blobs --disable-sdl"
EXTRA_OECONF_remove = "--enable-sdl"
EXTRA_OECONF_class-nativesdk_remove = "--enable-sdl"

EXTRA_OECONF += "--enable-system"

BBCLASSEXTEND = "native nativesdk"

# QEMU_TARGETS is overridable variable
QEMU_TARGETS ?= "arm aarch64 i386 mips mipsel mips64 mips64el ppc sh4 x86_64"

# possible arch values are arm aarch64 mips mipsel mips64 mips64el ppc ppc64 ppc64abi32
# ppcemb armeb alpha sparc32plus i386 x86_64 cris m68k microblaze sparc sparc32
# sparc32plus

def get_qemu_target_list(d):
    import bb
    archs = d.getVar('QEMU_TARGETS', True).split()
    tos = d.getVar('HOST_OS', True)
    softmmuonly = ""
    for arch in ['mips64', 'mips64el', 'ppcemb']:
        if arch in archs:
            softmmuonly += arch + "-softmmu,"
            archs.remove(arch)
    linuxuseronly = ""
    for arch in ['armeb', 'alpha', 'ppc64abi32', 'sparc32plus']:
        if arch in archs:
            linuxuseronly += arch + "-linux-user,"
            archs.remove(arch)
    if 'linux' not in tos:
        return softmmuonly + ''.join([arch + "-softmmu" + "," for arch in archs]).rstrip(',')
    return softmmuonly + linuxuseronly + ''.join([arch + "-linux-user" + "," + arch + "-softmmu" + "," for arch in archs]).rstrip(',')

EXTRA_OECONF += "--target-list=${@get_qemu_target_list(d)} --disable-werror \
		 --disable-bluez --disable-libiscsi --with-system-pixman \
		 --extra-cflags='${CFLAGS}'"

EXTRA_OECONF_class-nativesdk += "--target-list=${@get_qemu_target_list(d)} --disable-werror \
				"
export LIBTOOL="${HOST_SYS}-libtool"

do_configure_prepend_class-native() {
	# Undo the -lX11 added by linker-flags.patch, don't assume that host has libX11 installed
	sed -i 's/-lX11//g' Makefile.target
}

do_configure_prepend_class-nativesdk() {
	if [ "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}" = "" ] ; then
		# Undo the -lX11 added by linker-flags.patch
		sed -i 's/-lX11//g' Makefile.target
	fi
}

do_configure() {
	# Handle distros such as CentOS 5 32-bit that do not have kvm support
	KVMOPTS="--disable-kvm"
	if [ "${PN}" != "qemu-native" -a "${PN}" != "nativesdk-qemu" ] \
	|| [ -f /usr/include/linux/kvm.h ] ; then
		KVMOPTS="--enable-kvm"
	fi

	${S}/configure --prefix=${prefix} --sysconfdir=${sysconfdir} --libexecdir=${libexecdir} \
		--localstatedir=${localstatedir} --disable-strip ${EXTRA_OECONF} $KVMOPTS
	test ! -e ${S}/target-i386/beginend_funcs.sh || chmod a+x ${S}/target-i386/beginend_funcs.sh
}

do_compile_ptest() {
	make buildtest-TESTS
}

do_install_ptest() {
	cp -rL ${B}/tests ${D}${PTEST_PATH}
	find ${D}${PTEST_PATH}/tests -type f -name "*.[Sshcod]" | xargs -i rm -rf {}

	cp ${S}/tests/Makefile ${D}${PTEST_PATH}/tests
}

do_install () {
	export STRIP="true"
	autotools_do_install
	install -d ${D}${datadir}/qemu
}

# The following fragment will create a wrapper for qemu-mips user emulation
# binary in order to work around a segmentation fault issue. Basically, by
# default, the reserved virtual address space for 32-on-64 bit is set to 4GB.
# This will trigger a MMU access fault in the virtual CPU. With this change,
# the qemu-mips works fine.
# IMPORTANT: This piece needs to be removed once the root cause is fixed!
do_install_append() {
	if [ -e "${D}/${bindir}/qemu-mips" ]; then
		create_wrapper ${D}/${bindir}/qemu-mips \
		QEMU_RESERVED_VA=0x0
	fi

	# Prevent QA warnings about installed ${localstatedir}/run
	if [ -d ${D}${localstatedir}/run ]; then
		rmdir ${D}${localstatedir}/run
	fi

	# some files belong into ${base_sbindir}
	install -d ${D}${base_bindir}
	mv ${D}${bindir}/qemu-ga ${D}${base_bindir}/qemu-ga

	# Install files/scripts from debian
	install -d ${D}${sysconfdir}/init.d
	install -d ${D}${base_libdir}/udev/rules.d
	install -m 0644 ${S}/debian/qemu-guest-agent.init ${D}${sysconfdir}/init.d/qemu-guest-agent
	install -m 0755 ${S}/debian/qemu-ifdown ${D}${sysconfdir}/qemu-ifdown
	install -m 0755 ${S}/debian/qemu-ifup.linux ${D}${sysconfdir}/qemu-ifup
	install -m 0644 ${S}/debian/qemu-system-common.udev \
		${D}/${base_libdir}/udev/rules.d/60-qemu-system-common.rules
	install -m 0644 ${S}/debian/qemu-system-x86.init \
		${D}${sysconfdir}/init.d/qemu-system-x86
	install -m 0755 ${S}/debian/kvm ${D}${bindir}/kvm
	install -m 0644 ${S}/debian/kvm.1 ${D}${mandir}/man1/kvm.1
}

PACKAGES =+ "${PN}-guest-agent ${PN}-kvm ${PN}-system-arm ${PN}-system-common \
	     ${PN}-system-mips ${PN}-systemd-ppc ${PN}-system-x86 ${PN}-utils ${PN}-user"

FILES_${PN}-guest-agent += " \
			${base_sbindir}/qemu-ga \
			${sysconfdir}/init.d/qemu-guest-agent"
FILES_${PN}-kvm += "${bindir}/kvm"
FILES_${PN}-system-common += " \
			${sysconfdir}/qemu-* \
			${bindir}/virtfs-proxy-helper \
			${base_libdir} \
			${libdir}/${DPN}/qemu-bridge-helper"
FILES_${PN}-dbg += "${libdir}/${DPN}/.debug"
FILES_${PN}-system-arm += "${bindir}/qemu-system-arm"
FILES_${PN}-system-mips += "${bindir}/qemu-system-mips*"
FILES_${PN}-system-ppc += "${bindir}/qemu-system-ppc*"
FILES_${PN}-system-x86 += "${bindir}/qemu-system-x86_64 \
			${bindir}/qemu-system-i386 \
			${sysconfdir}/${PN}/target-x86_64.conf \
			${sysconfdir}/init.d/qemu-system-x86"
FILES_${PN}-utils += " \
			${bindir}/${PN}-io \
			${bindir}/${PN}-img \
			${bindir}/${PN}-ndb"
FILES_${PN}-user += "${bindir}/${PN}-*"

# END of qemu-mips workaround

PACKAGECONFIG ??= " \
	fdt sdl alsa virtfs \
	${@bb.utils.contains('DISTRO_FEATURES', 'xen', 'xen', '', d)} \
        "
PACKAGECONFIG_class-native ??= "fdt alsa uuid"
PACKAGECONFIG_class-nativesdk ??= "fdt sdl"
NATIVEDEPS = ""
NATIVEDEPS_class-native = "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'libxext-native', '',d)}"
PACKAGECONFIG[sdl] = "--enable-sdl,--disable-sdl,libsdl ${NATIVEDEPS},"
PACKAGECONFIG[virtfs] = "--enable-virtfs --enable-attr,--disable-virtfs,libcap attr,"
PACKAGECONFIG[aio] = "--enable-linux-aio,--disable-linux-aio,libaio,"
PACKAGECONFIG[xfs] = "--enable-xfsctl,--disable-xfsctl,xfsprogs,"
PACKAGECONFIG[xen] = "--enable-xen,--disable-xen,xen,xen-libxenstore xen-libxenctrl xen-libxenguest"
PACKAGECONFIG[vnc-tls] = "--enable-vnc --enable-vnc-tls,--disable-vnc-tls, gnutls,"
PACKAGECONFIG[vnc-sasl] = "--enable-vnc --enable-vnc-sasl,--disable-vnc-sasl,cyrus-sasl,"
PACKAGECONFIG[vnc-jpeg] = "--enable-vnc --enable-vnc-jpeg,--disable-vnc-jpeg,libjpeg-turbo,"
PACKAGECONFIG[vnc-png] = "--enable-vnc --enable-vnc-png,--disable-vnc-png,libpng,"
PACKAGECONFIG[libcurl] = "--enable-curl,--disable-curl,libcurl,"
PACKAGECONFIG[nss] = "--enable-smartcard-nss,--disable-smartcard-nss,nss,"
PACKAGECONFIG[uuid] = "--enable-uuid,--disable-uuid,util-linux,"
PACKAGECONFIG[curses] = "--enable-curses,--disable-curses,ncurses,"
PACKAGECONFIG[gtk+] = "--enable-gtk --enable-vte,--disable-gtk --disable-vte,gtk+ libvte,"
PACKAGECONFIG[libcap-ng] = "--enable-cap-ng,--disable-cap-ng,libcap-ng,"
PACKAGECONFIG[ssh2] = "--enable-libssh2,--disable-libssh2,libssh2,"
PACKAGECONFIG[libusb] = "--enable-libusb,--disable-libusb,libusb1"
PACKAGECONFIG[fdt] = "--enable-fdt,--disable-fdt,dtc"
PACKAGECONFIG[alsa] = ",,alsa-lib"
PACKAGECONFIG[lzo] = "--enable-lzo,--disable-lzo,lzo"
PACKAGECONFIG[numa] = "--enable-numa,--disable-numa,numactl"

EXTRA_OECONF += "${@bb.utils.contains('PACKAGECONFIG', 'alsa', '--audio-drv-list=oss,alsa', '', d)}"

# Qemu target will not build in world build for ARM or Mips
BROKEN_qemuarm = "1"
BROKEN_qemumips64 = "1"
BROKEN_qemumips = "1"

INSANE_SKIP_${PN} = "arch"
