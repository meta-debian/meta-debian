#
# base recipe: meta/recipes-devtools/mtd/mtd-utils_git.bb
# base branch: master
# base commit: ae69b1fe8e61fdb29a6c95d5365c36876710c116
#

SUMMARY = "Tools for managing memory technology devices"
DESCRIPTION = "Memory Technology Device Utilities \
Utilities for manipulating memory technology devices, such as flash \
memory, Disk-On-Chip, or ROM.  Includes mkfs.jffs2, a tool to create \
JFFS2 (journaling flash file system) filesystems."
HOMEPAGE = "http://www.linux-mtd.infradead.org/"
PR = "r0"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=0636e73ff0215e8d672dc4c32c317bb3 \
    file://include/common.h;beginline=1;endline=17;md5=ba05b07912a44ea2bf81ce409380049c \
"

DEPENDS = "zlib lzo util-linux"

inherit debian-package
PV = "1.5.1"
DEBIAN_PATCH_TYPE = "nopatch"

# xattr support creates an additional compile-time dependency on acl because
# the sys/acl.h header is needed. libacl is not needed and thus enabling xattr
# regardless whether acl is enabled or disabled in the distro should be okay.
PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'xattr', 'xattr', '', d)}"
PACKAGECONFIG[xattr] = ",,acl,"

EXTRA_OEMAKE = " \
    'CC=${CC}' 'RANLIB=${RANLIB}' 'AR=${AR}' 'CFLAGS=${CFLAGS} \
    ${@bb.utils.contains('PACKAGECONFIG', 'xattr', '', '-DWITHOUT_XATTR', d)} \
    -I${S}/include' 'BUILDDIR=${S}' \
"

do_install () {
	oe_runmake install DESTDIR=${D} SBINDIR=${sbindir} MANDIR=${mandir} INCLUDEDIR=${includedir}
}

RPROVIDES_${PN} = "mtd-tools"

BBCLASSEXTEND = "native"
