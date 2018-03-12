SUMMARY = "GPT fdisk text-mode partitioning tool"
DESCRIPTION = "GPT fdisk (aka gdisk) is a text-mode partitioning \
tool that works on Globally Unique Identifier \
(GUID) Partition Table (GPT) disks, rather than \
on the more common (through 2009) \
Master Boot Record (MBR) partition tables."
HOMEPAGE = "http://sourceforge.net/projects/gptfdisk/"

inherit debian-package
PV = "0.8.10"

LICENSE = "GPL-2.0+"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

DEPENDS = "popt util-linux ncurses"

do_compile() {
	oe_runmake CC="${CC}" CXX="${CXX}"
}

do_install() {
	install -D gdisk  ${D}${base_sbindir}/gdisk
	install -D sgdisk ${D}${base_sbindir}/sgdisk
	install -D cgdisk ${D}${base_sbindir}/cgdisk
	install -D fixparts ${D}${base_sbindir}/fixparts
}
