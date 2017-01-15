#
# base recipe: meta/recipes-devtools/syslinux/syslinux_6.03.bb
# base branch: jethro
#

SUMMARY = "collection of bootloaders"
DESCRIPTION = "syslinux is a suite of bootloaders, currently supporting DOS FAT and NTFS \
filesystems (SYSLINUX), Linux ext2/ext3/ext4, btrfs, and xfs filesystems \
(EXTLINUX), PXE network boots (PXELINUX), or ISO 9660 CD-ROMs (ISOLINUX)."
HOMEPAGE = "http://www.syslinux.org/"

inherit debian-package
PV = "6.03+dfsg"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3 \
    file://README;beginline=34;endline=40;md5=f7249a750bc692d1048b2626752aa415 \
"

DEPENDS = "nasm-native util-linux e2fsprogs"

COMPATIBLE_HOST = '(x86_64|i.86).*-(linux|freebsd.*)'
# Don't let the sanity checker trip on the 32 bit real mode BIOS binaries
INSANE_SKIP_${PN}-common = "arch"

EXTRA_OEMAKE = " \
	BINDIR=${bindir} SBINDIR=${sbindir} LIBDIR=${libdir} \
	DATADIR=${datadir} MANDIR=${mandir} INCDIR=${includedir} \
"

# syslinux uses $LD for linking, strip `-Wl,' so it can work
export LDFLAGS = "`echo $LDFLAGS | sed 's/-Wl,//g'`"

do_configure() {
	# drop win32 targets or build fails
	sed -e 's,win32/\S*,,g' -i Makefile

	# clean installer executables
	oe_runmake clean firmware="efi32" EFIINC="${includedir}"
	# NOTE: There is a temporary work around above to specify
	#       the efi32 as the firmware else the pre-built bios
	#       files get erased contrary to the doc/distib.txt
	#       In the future this should be "bios" and not "efi32".
}

do_compile() {
	# Make sure the recompile is OK.
	# Though the ${B} should always exist, still check it before find and rm.
	[ -d "${B}" ] && find ${B} -name '.*.d' -type f -exec rm -f {} \;

	DATE=`date +%Y%m%d`
	VERSION=`cat ${S}/version | cut -d' ' -f1`
	# Rebuild only the installer; keep precompiled bootloaders
	# as per author's request (doc/distrib.txt)
	oe_runmake CC="${CC} ${CFLAGS}" LDFLAGS="${LDFLAGS}" firmware="bios" \
	           DATE="${DATE}" VERSION="${VERSION}" \
	           installer
}

do_install() {
	oe_runmake CC="${CC} ${CFLAGS}" install INSTALLROOT="${D}" firmware="bios"

	# removing keytab-lilo, already part of the lilo package
	rm -f ${D}${bindir}/keytab-lilo

	# removing ms-dos executables
	rm -f ${D}${datadir}/syslinux/*.com
	rm -rf ${D}${datadir}/syslinux/dosutil

	# removing com32 files
	rm -rf ${D}${datadir}/syslinux/com32

	# removing diag files
	rm -rf ${D}${datadir}/syslinux/diag

	# moving files for FHS compliance
	mv ${D}${sbindir}/* ${D}${bindir}
	rmdir --ignore-fail-on-non-empty ${D}${sbindir}

	install -d ${D}${libdir}
	mv ${D}${datadir}/syslinux ${D}${libdir}

	# moving syslinux mbr file location
	install -d ${D}${libdir}/syslinux/mbr
	mv ${D}${libdir}/syslinux/*mbr*.bin ${D}${libdir}/syslinux/mbr

	# moving syslinux modules file location
	install -d ${D}${libdir}/syslinux/modules/bios
	mv ${D}${libdir}/syslinux/*.c32 ${D}${libdir}/syslinux/modules/bios

	# moving extlinux bootloader files
	install -d ${D}${libdir}/EXTLINUX
	cp ${D}${libdir}/syslinux/mbr/*mbr.bin ${D}${libdir}/EXTLINUX

	rm -f ${D}${libdir}/syslinux/mbr/*_c.bin
	rm -f ${D}${libdir}/syslinux/mbr/*_f.bin

	# moving isolinux bootloader files
	install -d ${D}${libdir}/ISOLINUX
	mv ${D}${libdir}/syslinux/isolinux.bin ${D}${libdir}/ISOLINUX
	mv ${D}${libdir}/syslinux/isohd*x.bin ${D}${libdir}/ISOLINUX

	rm -f ${D}${libdir}/syslinux/isolinux-debug.bin
	rm -f ${D}${libdir}/syslinux/isohd*x_c.bin
	rm -f ${D}${libdir}/syslinux/isohd*x_f.bin

	# moving pxelinux bootloader files
	install -d ${D}${libdir}/PXELINUX
	mv ${D}${libdir}/syslinux/*pxelinux.0 ${D}${libdir}/PXELINUX

	rm -f ${D}${libdir}/syslinux/gpxelinuxk.0

	install -d ${D}${datadir}/syslinux/
	install -m 644 ${S}/bios/core/ldlinux.sys ${D}${datadir}/syslinux/
	install -m 644 ${S}/bios/core/ldlinux.bss ${D}${datadir}/syslinux/
	ln -s ../../lib/syslinux/mbr/mbr.bin ${D}${datadir}/syslinux/mbr.bin
}

PACKAGES =+ "${PN}-common extlinux isolinux pxelinux"
PACKAGES += "${PN}-utils"

FILES_${PN}-common = "${libdir}/syslinux/*"
FILES_extlinux = " \
    ${bindir}/extlinux \
    ${libdir}/EXTLINUX/* \
"
FILES_isolinux = "${libdir}/ISOLINUX/*"
FILES_pxelinux = "${libdir}/PXELINUX/*"

FILES_${PN} = "${bindir}/syslinux ${datadir}"
FILES_${PN}-utils = "${bindir}/*"
FILES_${PN}-staticdev += "${datadir}/syslinux/com32/lib*.a"

RDEPENDS_${PN} += "mtools"

BBCLASSEXTEND = "native nativesdk"
