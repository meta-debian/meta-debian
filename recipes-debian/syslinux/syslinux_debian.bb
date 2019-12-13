#
# base recipe: meta/recipes-devtools/syslinux/syslinux_6.04-pre2.bb
# base branch: warrior
#

SUMMARY = "Multi-purpose linux bootloader"
HOMEPAGE = "http://www.syslinux.org/"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3 \
                    file://README;beginline=35;endline=41;md5=558f2c71cb1fb9ba511ccd4858e48e8a"

inherit debian-package
require recipes-debian/sources/syslinux.inc

inherit perlnative

# If you really want to run syslinux, you need mtools.  We just want the
# ldlinux.* stuff for now, so skip mtools-native
DEPENDS = "nasm-native util-linux e2fsprogs"

FILESEXTRAPATHS =. "${COREBASE}/meta/recipes-devtools/syslinux/syslinux:"

SRC_URI += "file://syslinux-remove-clean-script.patch \
           file://0001-linux-syslinux-support-ext2-3-4-device.patch \
           file://0002-linux-syslinux-implement-open_ext2_fs.patch \
           file://0003-linux-syslinux-implement-install_to_ext2.patch \
           file://0004-linux-syslinux-add-ext_file_read-and-ext_file_write.patch \
           file://0005-linux-syslinux-implement-handle_adv_on_ext.patch \
           file://0006-linux-syslinux-implement-write_to_ext-and-add-syslin.patch \
           file://0007-linux-syslinux-implement-ext_construct_sectmap_fs.patch \
           file://0008-libinstaller-syslinuxext-implement-syslinux_patch_bo.patch \
           file://0009-linux-syslinux-implement-install_bootblock.patch \
           file://correct_LDFLAGS_to_add_more_option.patch \
           "

COMPATIBLE_HOST = '(x86_64|i.86).*-(linux|freebsd.*)'
# Don't let the sanity checker trip on the 32 bit real mode BIOS binaries
INSANE_SKIP_${PN}-misc = "arch"
INSANE_SKIP_${PN}-chain = "arch"

EXTRA_OEMAKE = " \
	BINDIR=${bindir} SBINDIR=${sbindir} LIBDIR=${libdir} \
	DATADIR=${datadir} MANDIR=${mandir} INCDIR=${includedir} \
"
do_configure() {
	# clean installer executables included in source tarball
	oe_runmake clean firmware="efi32" EFIINC="${includedir}"
	# NOTE: There is a temporary work around above to specify
	#	the efi32 as the firmware else the pre-built bios
	#	files get erased contrary to the doc/distib.txt
	#	In the future this should be "bios" and not "efi32".
}

do_compile() {
	# syslinux uses $LD for linking, strip `-Wl,' so it can work
	export LDFLAGS="-L${STAGING_LIBDIR}"

	sed -i 's@$(CC) $(LDFLAGS)@$(BUILD_CC) $(BUILD_LDFLAGS)@g' ${S}/lzo/Makefile

	DATE=`date +%Y%m%d`
	# Make sure the recompile is OK.
	# Though the ${B} should always exist, still check it before find and rm.
	[ -d "${B}" ] && find ${B} -name '.*.d' -type f -exec rm -f {} \;

	oe_runmake CC="${CC} ${CFLAGS}" LD="${LD}" firmware="bios" DATE="${DATE}" bios
}

do_install() {
	oe_runmake CC="${CC} ${CFLAGS}" LD="${LD}" firmware="bios" install INSTALLROOT="${D}"

	install -d ${D}${datadir}/syslinux/
	install -m 644 ${S}/bios/core/ldlinux.sys ${D}${datadir}/syslinux/
	install -m 644 ${S}/bios/core/ldlinux.bss ${D}${datadir}/syslinux/
	install -m 755 ${S}/bios/linux/syslinux-nomtools ${D}${bindir}/
}

PACKAGES += "${PN}-nomtools ${PN}-extlinux ${PN}-mbr ${PN}-chain ${PN}-pxelinux ${PN}-isolinux ${PN}-misc"

RDEPENDS_${PN} += "mtools"
RDEPENDS_${PN}-nomtools += "libext2fs"
RDEPENDS_${PN}-misc += "perl"

FILES_${PN} = "${bindir}/syslinux"
FILES_${PN}-nomtools = "${bindir}/syslinux-nomtools"
FILES_${PN}-extlinux = "${sbindir}/extlinux"
FILES_${PN}-mbr = "${datadir}/${BPN}/mbr.bin"
FILES_${PN}-chain = "${datadir}/${BPN}/chain.c32"
FILES_${PN}-isolinux = "${datadir}/${BPN}/isolinux.bin"
FILES_${PN}-pxelinux = "${datadir}/${BPN}/pxelinux.0"
FILES_${PN}-dev += "${datadir}/${BPN}/com32/lib*${SOLIBS} ${datadir}/${BPN}/com32/include ${datadir}/${BPN}/com32/com32.ld"
FILES_${PN}-staticdev += "${datadir}/${BPN}/com32/lib*.a ${libdir}/${BPN}/com32/lib*.a"
FILES_${PN}-misc = "${datadir}/${BPN}/* ${libdir}/${BPN}/* ${bindir}/*"

BBCLASSEXTEND = "native nativesdk"
PARALLEL_MAKE = ""
