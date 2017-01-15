SUMMARY = "Identify, read, write, erase, and verify BIOS/ROM/flash chips"
DESCRIPTION = "\
flashrom is a tool for identifying, reading, writing, verifying and erasing \
flash chips. It's often used to flash BIOS/EFI/coreboot/firmware/optionROM \
images in-system using a supported mainboard, but it also supports flashing of \
network cards (NICs), SATA controller cards, and other external devices which \
can program flash chips. \
It supports a wide range of DIP32, PLCC32, DIP8, SO8/SOIC8, TSOP32/40/48, \
and BGA chips, which use various protocols such as LPC, FWH, parallel \
flash, or SPI. \
The tool can be used to flash BIOS/firmware images for example -- be it \
proprietary BIOS images or coreboot (previously known as LinuxBIOS) images. \
It can also be used to read the current existing BIOS/firmware from a \
flash chip. \
"
HOMEPAGE = "http://www.flashrom.org"
PR = "r0"
inherit debian-package
PV = "0.9.7+r1782"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=751419260aa954499f7abaabaa882bbe"
inherit autotools-brokensep
DEPENDS += "pciutils"
EXTRA_OEMAKE += "PREFIX=${prefix}"
do_install_append() {
	install -d ${D}${base_libdir}/udev/rules.d
	install -m 0644 ${S}/util/z60_flashrom.rules \
		${D}${base_libdir}/udev/rules.d/60-flashrom.rules
}
PARALLEL_MAKE = ""
