require recipes-devtools/qemu/qemu.inc

FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-devtools/qemu/qemu:\
${COREBASE}/meta/recipes-devtools/qemu/files:\
" 

inherit debian-package
DEBIAN_SECTION = "otherosfs"
DPR = "0"

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=441c28d2cf86e15a37fa47e15a72fbac \
	   file://COPYING.LIB;md5=79ffa0ec772fa86740948cb7327a0cc7"

#Two patches no need to apply because of difference source code version:
#-fxrstorssefix.patch
#-no-strip.patch
#Fix: Qemu-Arm-versatilepb-Add-memory-size-checking.patch
#to apply patch successfully
#
SRC_URI += "\
file://powerpc_rom.bin \
file://larger_default_ram_size.patch \
file://disable-grabs.patch \
file://qemu-enlarge-env-entry-size.patch \
file://Qemu-Arm-versatilepb-Add-memory-size-checking_debian.patch \
"

#
#This recipe doesn't create BIOS images which may be required.
#BIOS images cannot be built on cross compiling.
# binary of BIOS should be downloaded from repository.
#
EXTRA_OECONF += "--disable-blobs"
EXTRA_OECONF_virtclass-nativesdk += "--disable-blobs"
