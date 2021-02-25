# Note, we can probably remove the lzma option as it has be replaced with xz,
# and I don't think the kernel supports it any more.
SUMMARY = "Tools for manipulating SquashFS filesystems"
SECTION = "base"

inherit debian-package

LICENSE = "GPL-2 & PD"
LIC_FILES_CHKSUM = "file://../COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://../../7zC.txt;beginline=12;endline=16;md5=2056cd6d919ebc3807602143c7449a7c \
"
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
