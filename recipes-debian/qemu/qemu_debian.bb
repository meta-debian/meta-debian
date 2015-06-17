require recipes-devtools/qemu/qemu.inc

FILESEXTRAPATHS_prepend = "\
${THISDIR}/files:${COREBASE}/meta/recipes-devtools/qemu/qemu:\
${COREBASE}/meta/recipes-devtools/qemu/files:\
" 

inherit debian-package
DEBIAN_SECTION = "otherosfs"
DPR = "0"

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=441c28d2cf86e15a37fa47e15a72fbac \
	   file://COPYING.LIB;md5=79ffa0ec772fa86740948cb7327a0cc7"

# qemu-bios-native provides BIOS images for qemu-system
DEPENDS_class-native += "qemu-bios-native"

#Two patches no need to apply because of difference source code version:
#-fxrstorssefix.patch
#-no-strip.patch
#Fix: Qemu-Arm-versatilepb-Add-memory-size-checking.patch
#to apply patch successfully
#
# replace-bios-256k-by-128k.patch:
#   bios-256k.bin is not provided by qemu-bios-native
SRC_URI += "\
file://powerpc_rom.bin \
file://larger_default_ram_size.patch \
file://disable-grabs.patch \
file://qemu-enlarge-env-entry-size.patch \
file://Qemu-Arm-versatilepb-Add-memory-size-checking_debian.patch \
file://replace-bios-256k-by-128k.patch \
"

#
#This recipe doesn't create BIOS images which may be required.
#BIOS images cannot be built on cross compiling.
# binary of BIOS should be downloaded from repository.
#
#Since environment doesn't have libsdl, so qemu should not depend
#on it.
#
EXTRA_OECONF += "--disable-blobs --disable-sdl"
EXTRA_OECONF_virtclass-nativesdk += "--disable-blobs --disable-sdl"
EXTRA_OECONF_remove = "--enable-sdl"
EXTRA_OECONF_virtclass-nativesdk_remove = "--enable-sdl"

do_install_append() {
	# Prevent QA warnings about installed ${localstatedir}/run
	if [ -d ${D}${localstatedir}/run ]; then
		rmdir ${D}${localstatedir}/run
	fi
}
