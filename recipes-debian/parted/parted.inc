SUMMARY = "disk partition manipulator"
DESCRIPTION = "GNU Parted is a program that allows you to create, destroy, resize,\n\
move, and copy disk partitions. This is useful for creating space\n\
for new operating systems, reorganizing disk usage, and copying data\n\
to new hard disks.\n\
.\n\
Parted currently supports DOS, Mac, Sun, BSD, GPT, MIPS, and PC98\n\
partitioning formats, as well as a "loop" (raw disk) type which\n\
allows use on RAID/LVM. It can detect and remove ASFS/AFFS/APFS,\n\
Btrfs, ext2/3/4, FAT16/32, HFS, JFS, linux-swap, UFS, XFS, and ZFS\n\
file systems. Parted also has the ability to create and modify file\n\
systems of some of these types, but using it to perform file system\n\
operations is now deprecated.\n\
.\n\
The nature of this software means that any bugs could cause massive\n\
data loss. While there are no such bugs known at the moment, they\n\
could exist, so please back up all important files before running\n\
it, and do so at your own risk."
HOMEPAGE = "http://www.gnu.org/software/parted"

inherit debian-package
PV = "3.2"
INC_PR = "r0"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=2f31b266d3440dd7ee50f92cf67d8e6c"

DEPENDS = "ncurses readline util-linux lvm2"

# syscalls.patch: Fix error:
#     | libparted/arch/linux.c:1700:22: error: unknown type name '_llseek'
#     | libparted/arch/linux.c:1701:32: error: unknown type name 'fd'
#     ...
# fix-doc-mandir.patch: Fix error:
#     | for po in `ls -1 ./*.pt_BR.po 2>/dev/null`; do \
#     |       make $(basename ${po%.pt_BR.po}); \
#     | done
#     | Makefile:970: *** Recursive variable 'mandir' references itself (eventually).  Stop.
SRC_URI += " \
    file://syscalls.patch \
    file://fix-doc-mandir.patch \
"

inherit autotools pkgconfig gettext texinfo

# Follow debian/rules
EXTRA_OECONF = " \
    --sbindir=${base_sbindir} \
    --enable-mtrace \
    --enable-shared \
    --disable-gcc-warnings \
    --enable-device-mapper \
"

do_install_append() {
	test -d ${D}${base_libdir} || install -d ${D}${base_libdir}

	# Follow debian/libparted2.install
	mv ${D}${libdir}/libparted${SOLIBS} ${D}${base_libdir}/

	# Follow debian/libparted-fs-resize0.install
	mv ${D}${libdir}/libparted-fs-resize${SOLIBS} ${D}${base_libdir}/

	# Follow debian/libparted-dev.links
	rel_lib_prefix=`echo ${libdir} | sed 's,\(^/\|\)[^/][^/]*,..,g'`
	ln -sf $rel_lib_prefix${base_libdir}/libparted.so.2 ${D}${libdir}/libparted.so
	ln -sf $rel_lib_prefix${base_libdir}/libparted-fs-resize.so.0 \
	       ${D}${libdir}/libparted-fs-resize.so

	# Follow debian/rules
	rm -f ${D}${libdir}/*.la
}
