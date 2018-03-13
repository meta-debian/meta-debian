SUMMARY = "Interact with the EFI Boot Manager"
DESCRIPTION = "This is a Linux user-space application to modify the Intel Extensible\n\
Firmware Interface (EFI) Boot Manager configuration. This application can\n\
create and destroy boot entries, change the boot order, change the next\n\
running boot option, and more.\n\
.\n\
Additional information about (U)EFI can be found at http://www.uefi.org/.\n\
.\n\
Note: efibootmgr requires that the kernel module efivars be loaded prior\n\
to use. 'modprobe efivars' should do the trick if it does not\n\
automatically load."

inherit debian-package
PV = "0.11.0"

LICENSE = "GPL-2.0+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3"

# efibootmgr 0.11.0 on Debian is built with kernel header 3.16.0
# which still provides linux/nvme.h.
# That header had been rename to nvme_ioctl.h from commit
# 9d99a8dda154f38307d43d9c9aa504bd3703d596
SRC_URI += "file://kernel_4.4_nvme_header.patch"

DEPENDS = "pciutils zlib efivar"

do_compile() {
	oe_runmake
}

do_install() {
	install -d ${D}${base_bindir}
	install -m 0755 ${B}/src/efibootmgr/efibootmgr ${D}${base_bindir}/
}
