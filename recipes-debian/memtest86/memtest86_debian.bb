SUMMARY = "thorough real-mode memory tester"
DESCRIPTION = "Memtest86 scans your RAM for errors.\n\
.\n\
This tester runs independently of any OS - it is run at computer\n\
boot-up, so that it can test *all* of your memory.  You may want to\n\
look at `memtester', which allows testing your memory within Linux,\n\
but this one won't be able to test your whole RAM.\n\
.\n\
It can output a list of bad RAM regions usable by the BadRAM kernel\n\
patch, so that you can still use you old RAM with one or two bad bits.\n\
.\n\
This is the last DFSG-compliant version of this software, upstream\n\
has opted for a proprietary development model starting with 5.0.  You\n\
may want to consider using memtest86+, which has been forked from an\n\
earlier version of memtest86, and provides a different set of\n\
features.  It is available in the memtest86+ package.\n\
.\n\
A convenience script is also provided to make a grub-legacy-based\n\
floppy or image."
HOMEPAGE = "http://www.memtest86.com/"

inherit debian-package
PV = "4.3.7"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://config.c;endline=9;md5=13c7c577f8c287d9457519739aea1898"

DEBIAN_PATCH_TYPE = "nopatch"
inherit autotools-brokensep

do_compile() {
	oe_runmake CC="${CC}" LD="${LD}" AS="${AS} -32"
}

# Base on debian/rules
do_install() {
	install -d ${D}${datadir}/lintian/overrides

	install -D -m 0755 ${S}/debian/make-memtest86-boot-floppy \
		${D}${bindir}/make-memtest86-boot-floppy
	install -D -m 0644 ${S}/memtest.bin ${D}/boot/memtest86.bin
	install -D -m 0644 ${S}/memtest ${D}${libdir}/memtest86/memtest86.elf
	install -D -m 0755 ${S}/debian/grub \
		${D}${sysconfdir}/grub.d/20_memtest86
	install -m 0644 ${S}/debian/lintian-overrides \
		${D}${datadir}/lintian/overrides/memtest86
}
FILES_${PN} += "/boot ${datadir}/lintian/overrides"

# memtest86.elf is ELF 32-bit LSB executable \
# add an INSANE_SKIP bypass check "arch" to avoid the QA error.
INSANE_SKIP_${PN} = "arch"

PARALLEL_MAKE = ""
