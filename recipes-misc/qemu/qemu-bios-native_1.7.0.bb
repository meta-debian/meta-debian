#
# qemu-bios-native
#
# This recipe provide BIOS images for qemu-native.
#
# Currently, qemu source package in Debian doesn't include BIOS images
# and tricky implementation is needed for cross-compiling BIOS images from
# several source packages like "openbios" and "seabios" by native recipes :-(
#
# Therefore, we TEMPORALLY use this recipe only for getting
# the BIOS images from vanilla qemu source.
# This should be replaced by only Debian sources in future.
#

PR = "r0"

inherit native

SRC_URI = "${MISC_GIT_URI}/${MISC_GIT_PREFIX}qemu.git;protocol=${MISC_GIT_PROTOCOL};branch=stable-2.1"
SRCREV = "c2b0926634cda378f634be62c616afbf03ca5890"
PV = "2.1.3+git${SRCPV}"

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=441c28d2cf86e15a37fa47e15a72fbac \
file://COPYING.LIB;endline=24;md5=c04def7ae38850e7d3ef548588159913 \
"

S = "${WORKDIR}/git"

# comes from ${S}/Makefile, the list of firmware blobs
BLOBS = " \
bios.bin sgabios.bin vgabios.bin vgabios-cirrus.bin \
vgabios-stdvga.bin vgabios-vmware.bin vgabios-qxl.bin \
acpi-dsdt.aml q35-acpi-dsdt.aml \
ppc_rom.bin openbios-sparc32 openbios-sparc64 openbios-ppc QEMU,tcx.bin \
pxe-e1000.rom pxe-eepro100.rom pxe-ne2k_pci.rom \
pxe-pcnet.rom pxe-rtl8139.rom pxe-virtio.rom \
efi-e1000.rom efi-eepro100.rom efi-ne2k_pci.rom \
efi-pcnet.rom efi-rtl8139.rom efi-virtio.rom \
qemu-icon.bmp qemu_logo_no_text.svg \
bamboo.dtb petalogix-s3adsp1800.dtb petalogix-ml605.dtb \
multiboot.bin linuxboot.bin kvmvapic.bin \
s390-zipl.rom \
s390-ccw.img \
spapr-rtas.bin slof.bin \
palcode-clipper \
"

do_configure() {
	:
}

do_compile() {
	:
}

do_install() {
	install -d ${D}${datadir}/qemu
	for blob in ${BLOBS}; do
		install -m 0644 ${S}/pc-bios/${blob} ${D}${datadir}/qemu
	done
}
