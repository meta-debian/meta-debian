# base recipe: meta/recipes-kernel/blktrace
# base branch: master
# base commit: 4972eddecde755045ec9598812f8c6c2b4fd243d

SUMMARY = "utilities for block layer IO tracing"
DESCRIPTION = "\
blktrace is a block layer IO tracing mechanism which provides detailed \
information about request queue operations up to user space. There are \
three major components that are provided:\
\n\
blktrace: A utility which transfers event traces from the kernel \
into either long-term on-disk storage, or provides direct formatted \
output (via blkparse).\
\n\
blkparse: A utility which formats events stored in files, or when \
run in live mode directly outputs data collected by blktrace.\
\n\
Running blktrace requires a patch to the Linux kernel which includes the \
kernel event logging interfaces, and patches to areas within the block \
layer to emit event traces.  These patches have been included in the main \
Linux kernel since 2.6.17-rc1, and the default Debian kernel since 2.6.23.\
"
HOMEPAGE = "http://brick.kernel.dk/snaps/"
SECTION = "utils"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=393a5ca445f6965873eca0259a17f833"

DEPENDS = "libaio"
RDEPENDS_${PN} += " lsb-base"
RSUGGESTS_${PN} += " python python-shell python-io"

SRC_URI = "file://ldflags.patch \
           file://0001-include-sys-types.h-for-dev_t-definition.patch \
"

inherit debian-package
PV = "1.0.5"
DEBIAN_PATCH_TYPE = "quilt"

EXTRA_OEMAKE = "\
    'CC=${CC}' \
    'CFLAGS=${CFLAGS}' \
    'LDFLAGS=${LDFLAGS}' \
"

# There are a few parallel issues:
# 1) ../rbtree.o: error adding symbols: Invalid operation
# collect2: error: ld returned 1 exit status
# Makefile:42: recipe for target 'btt' failed
# 2) git/blkiomon.c:216: undefined reference to `rb_insert_color'
# collect2: error: ld returned 1 exit status
# Makefile:27: recipe for target 'blkparse' failed
# 3) ld: rbtree.o: invalid string offset 128 >= 125 for section `.strtab'
# 4) btreplay.o: file not recognized: File truncated
# collect2: error: ld returned 1 exit status
# btreplay/btreplay.c:47:18: fatal error: list.h: No such file or directory
PARALLEL_MAKE = ""

FILES_${PN} += " ${sbindir}"

do_install() {
	oe_runmake ARCH="${ARCH}" prefix=${prefix} \
		mandir=${mandir} DESTDIR=${D} install

	# move admin-only stuff to /usr/sbin
	install -m 0755 -d ${D}${sbindir} && \
		mv ${D}${bindir}/btrace    ${D}${sbindir} && \
		mv ${D}${bindir}/blktrace  ${D}${sbindir} && \
		mv ${D}${bindir}/btreplay  ${D}${sbindir} && \
		mv ${D}${bindir}/btrecord  ${D}${sbindir} 

	# fix name of bno_plot
	mv ${D}${bindir}/bno_plot.py ${D}${bindir}/bno_plot

	install -m 0755 -d ${D}${sysconfdir}/init.d && \
		install -m 0755 ${S}/debian/blktrace.mountdebugfs.init \
			${D}${sysconfdir}/init.d/mountdebugfs
}
