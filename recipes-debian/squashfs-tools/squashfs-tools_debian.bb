# Note, we can probably remove the lzma option as it has be replaced with xz,
# and I don't think the kernel supports it any more.
SUMMARY = "Tools for manipulating SquashFS filesystems"
SECTION = "base"

inherit debian-package

LICENSE = "GPL-2"
LIC_FILES_CHKSUM = "file://squashfs_fs.h;beginline=6;endline=22;md5=ba5187034cd4f38eeaa654bb8f28af9f"

DEPENDS = "attr zlib xz lzo lz4"

PV = "4.2+20130409"

# EXTRA_OEMAKE is typically: -e MAKEFLAGS=
# the -e causes problems as CFLAGS is modified in the Makefile, so
# we redefine EXTRA_OEMAKE here
EXTRA_OEMAKE = "MAKEFLAGS= XZ_SUPPORT=1 LZO_SUPPORT=1 LZ4_SUPPORT=1"

do_compile() {
	oe_runmake mksquashfs unsquashfs
}
do_install () {
	install -d ${D}${sbindir}
	install -m 0755 mksquashfs ${D}${sbindir}/
	install -m 0755 unsquashfs ${D}${sbindir}/
}

ARM_INSTRUCTION_SET = "arm"

BBCLASSEXTEND = "native nativesdk"
