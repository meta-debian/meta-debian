require recipes-devtools/qemu/qemu.inc

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=441c28d2cf86e15a37fa47e15a72fbac \
    file://COPYING.LIB;endline=24;md5=c04def7ae38850e7d3ef548588159913 \
"

inherit debian-package
require recipes-debian/sources/qemu.inc
DEBIAN_UNPACK_DIR = "${WORKDIR}/${BPN}-3.1.0"

FILESPATH_append = ":${COREBASE}/meta/recipes-devtools/qemu/qemu"
SRC_URI += " \
    file://powerpc_rom.bin \
    file://0003-Add-subpackage-ptest-which-runs-all-unit-test-cases-.patch \
    file://run-ptest \
    file://0004-qemu-Add-addition-environment-space-to-boot-loader-q.patch \
    file://0005-qemu-disable-Valgrind.patch \
    file://0006-qemu-Limit-paths-searched-during-user-mode-emulation.patch \
    file://0007-qemu-native-set-ld.bfd-fix-cflags-and-set-some-envir.patch \
    file://0008-chardev-connect-socket-to-a-spawned-command.patch \
    file://0009-apic-fixup-fallthrough-to-PIC.patch \
    file://0010-linux-user-Fix-webkitgtk-hangs-on-32-bit-x86-target.patch \
    file://0011-Revert-linux-user-fix-mmap-munmap-mprotect-mremap-sh.patch \
    file://0001-Add-a-missing-X11-include.patch \
    file://0001-egl-headless-add-egl_create_context.patch \
"

# qemu source code from Debian does not provide these blobs,
# but seabios and ipxe does.
# Avoid error:
#   | install: cannot stat '.../qemu-system-native/3.1-r0/qemu-3.1.0/pc-bios/bios.bin': No such file or directory
FIRMWAREPATH = "${datadir}/qemu:${datadir}/seabios:${libdir}/ipxe/qemu"
EXTRA_OECONF += "--disable-blobs \
                 --firmwarepath=${FIRMWAREPATH} \
                 "
