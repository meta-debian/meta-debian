include grub2.inc

EXTRA_OECONF += "--with-platform=efi"

RDEPENDS_${PN} += "efibootmgr"
