#
# base recipe: meta/recipes-bsp/grub/grub_git.bb
#              meta/recipes-bsp/grub/grub2.inc
# base branch: daisy
#

PR = "r0"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

inherit debian-package autotools gettext

# autogen.sh-exclude-pc:
#	Exclude the .pc from po/POTFILES.in
#	since quilt uses "patch --backup",
# 	which will create the backup file under .pc,
#	this may cause unexpected errors (OE specific).
# grub-2.00-add-oe-kernel.patch:
#	Our kernel's name is bzImage, we need add it to grub.d
#	so grub-mkconfig and grub-install can work correctly (OE specific).
# grub-2.00-fpmath-sse-387-fix.patch:
#	Avoid error: SSE instruction set disabled, using 387 arithmetics [-Werror]
SRC_URI += " \
	file://autogen.sh-exclude-pc.patch \
	file://grub-2.00-add-oe-kernel.patch \
	file://grub-2.00-fpmath-sse-387-fix.patch \
"

DEFAULT_PREFERENCE = "-1"
DEFAULT_PREFERENCE_arm = "1"

DEPENDS = "flex-native bison-native xz"

PACKAGECONFIG ??= ""
PACKAGECONFIG[grub-mount] = "--enable-grub-mount,--disable-grub-mount,fuse"
PACKAGECONFIG[device-mapper] = "--enable-device-mapper,--disable-device-mapper,lvm2"

COMPATIBLE_HOST = '(x86_64.*|i.86.*|arm.*|aarch64.*)-(linux.*|freebsd.*)'

# configure.ac has code to set this automagically from the target tuple
# but the OE freeform one (core2-foo-bar-linux) don't work with that.

GRUBPLATFORM_arm = "uboot"
GRUBPLATFORM_aarch64 = "efi"
GRUBPLATFORM ??= "pc"

EXTRA_OECONF = "--with-platform=${GRUBPLATFORM} --disable-grub-mkfont --program-prefix='' \
                --enable-liblzma=no --enable-device-mapper=no --enable-libzfs=no"

do_configure_prepend() {
	( cd ${S}
	${S}/autogen.sh )
}

# value for GRUB_CMDLINE_LINUX_DEFAULT in /etc/default/grub
DEFAULT_CMDLINE ?= "quiet"
do_install_append () {
	install -d ${D}${sysconfdir}/default
	sed -i "s/@DEFAULT_CMDLINE@/${DEFAULT_CMDLINE}/g" ${S}/debian/default/grub
	install -m 0644 ${S}/debian/default/grub ${D}${sysconfdir}/default/

	install -d ${D}${sbindir}
	install -m 0755 ${S}/debian/update-grub ${D}${sbindir}
}

# grub and grub-efi's sysroot/${datadir}/grub/grub-mkconfig_lib are
# conflicted, remove it since no one uses it.
SYSROOT_PREPROCESS_FUNCS_class-target += "remove_sysroot_mkconfig_lib"
remove_sysroot_mkconfig_lib() {
    rm -r "${SYSROOT_DESTDIR}${datadir}/grub/grub-mkconfig_lib"
}

# debugedit chokes on bare metal binaries
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

FILES_${PN} += "${libdir}/grub ${datadir}/grub"
RDEPENDS_${PN} = "diffutils freetype"

FILES_${PN}-dbg += "${libdir}/grub/*/.debug"

# Sometimes, ELF type of bootloader binary does not match the target architecture,
# bypass check "arch" to avoid the QA error.
INSANE_SKIP_${PN} = "arch"
INSANE_SKIP_${PN}-dbg = "arch"
