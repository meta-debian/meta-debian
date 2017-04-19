#
# Base recipe: meta/recipes-devtools/e2fsprogs/e2fsprogs_1.42.9.bb
# Base branch: daisy
#

SUMMARY = "Ext2 Filesystem Utilities"
DESCRIPTION = "The Ext2 Filesystem Utilities (e2fsprogs) contain all of the \
standard utilities for creating, fixing, configuring , and debugging ext2 filesystems."
HOMEPAGE = "http://e2fsprogs.sourceforge.net/"

PR = "r0"

inherit debian-package
PV = "1.42.12"

LICENSE = "GPLv2 & LGPLv2 & BSD & MIT"

LIC_FILES_CHKSUM = "\
file://COPYING;md5=b48f21d765b875bd10400975d12c1ca2 \
file://lib/ext2fs/ext2fs.h;beginline=1;endline=9;md5=596a8dedcb4e731c6b21c7a46fba6bef \
file://lib/e2p/e2p.h;beginline=1;endline=7;md5=8a74ade8f9d65095d70ef2d4bf48e36a \
file://lib/uuid/uuid.h.in;beginline=1;endline=32;md5=dbb8079e114a5f841934b99e59c8820a \
file://lib/uuid/COPYING;md5=58dcd8452651fc8b07d1f65ce07ca8af \
file://lib/et/et_name.c;beginline=1;endline=11;md5=ead236447dac7b980dbc5b4804d8c836 \
file://lib/ss/ss.h;beginline=1;endline=20;md5=6e89ad47da6e75fecd2b5e0e81e1d4a6 \
"

DEPENDS = "util-linux"

inherit autotools gettext pkgconfig multilib_header update-alternatives

# Exclude already applied patch:
# 0001-e2fsprogs-fix-cross-compilation-problem.patch
SRC_URI += " \
file://acinclude.m4 \
file://remove.ldconfig.call.patch \
file://fix-icache.patch \
file://quiet-debugfs.patch \
file://0001-mke2fs-add-the-ability-to-copy-files-from-a-given-di.patch \
file://0002-misc-create_inode.c-copy-files-recursively.patch \
file://0003-misc-create_inode.c-create-special-file.patch \
file://0004-misc-create_inode.c-create-symlink.patch \
file://0005-misc-create_inode.c-copy-regular-file.patch \
file://0006-misc-create_inode.c-create-directory.patch \
file://0007-misc-create_inode.c-set-owner-mode-time-for-the-inod.patch \
file://0008-mke2fs.c-add-an-option-d-root-directory_debian.patch \
file://0009-misc-create_inode.c-handle-hardlinks.patch \
file://0010-debugfs-use-the-functions-in-misc-create_inode.c_debian.patch \
file://0011-mke2fs.8.in-update-the-manual-for-the-d-option.patch \
file://misc-mke2fs.c-return-error-when-failed-to-populate-fs.patch \
"

COMMON_CONF_FLAGS = " \
		--disable-e2initrd-helper \
		--enable-quota \
		--infodir=${infodir} \
		--enable-symlink-install \
"
STD_CONF_FLAGS = " \
		--enable-elf-shlibs \
"
UTIL_CONF_FLAGS = " \
		--disable-fsck \
		--disable-libblkid \
		--disable-libuuid \
		--disable-uuidd \
"
EXTRA_OECONF += "\
		${COMMON_CONF_FLAGS} \
		${STD_CONF_FLAGS} \
		${UTIL_CONF_FLAGS} \
		--build=${BUILD_SYS} \
		--host=${HOST_SYS} \
		--libdir=${base_libdir} \
		--sbindir=${base_sbindir} \
"

# Automake has dropped such macro, use "mkdir -p" instead
do_configure_prepend () {
	cp ${WORKDIR}/acinclude.m4 ${S}/
	sed -i -e "s:AM_MKINSTALLDIRS:AM_PROG_MKDIR_P:" ${S}/configure.in
	sed -i -e "s:MKINSTALLDIRS = .*:MKINSTALLDIRS = @MKDIR_P@:" \
							${S}/MCONFIG.in
}

do_install_class-target () {
	oe_runmake 'DESTDIR=${D}' install
	oe_runmake 'DESTDIR=${D}' install-libs
	#Some files belong to libdir
	if [ ! ${D}${libdir} -ef ${D}${base_libdir} ]; then
		install -d ${D}${libdir}
		mv ${D}${base_libdir}/pkgconfig ${D}${libdir}
		# Refine softlink in class-target to avoid finding libraries in host system
		# while building other packages which use e2fsprogs's libraries.
		rm ${D}${base_libdir}/libe2p.so
		ln -sf ../../${base_libdir}/libe2p.so.2 ${D}${libdir}/libe2p.so
		rm ${D}${base_libdir}/libcom_err.so
		ln -sf ../../${base_libdir}/libcom_err.so.2 ${D}${libdir}/libcom_err.so
		rm ${D}${base_libdir}/libext2fs.so
		ln -sf ../../${base_libdir}/libext2fs.so.2 ${D}${libdir}/libext2fs.so
		rm ${D}${base_libdir}/libss.so
		ln -sf ../../${base_libdir}/libss.so.2 ${D}${libdir}/libss.so
		mv ${D}${base_libdir}/*.a ${D}${libdir}
	fi
	#Some files belong to sbindir
	install -d ${D}${sbindir}
	mv ${D}${base_sbindir}/e2freefrag ${D}${sbindir}
	mv ${D}${base_sbindir}/e4defrag ${D}${sbindir}
	mv ${D}${base_sbindir}/filefrag ${D}${sbindir}
	mv ${D}${base_sbindir}/mklost+found ${D}${sbindir}

	mv ${D}${base_sbindir}/mkfs.ext2 ${D}${base_sbindir}/mkfs.ext2.${DPN}

	oe_multilib_header ext2fs/ext2_types.h
}

do_install_class-native () {
	oe_runmake 'DESTDIR=${D}' install
	oe_runmake 'DESTDIR=${D}' install-libs
	#Some files belong to libdir
	if [ ! ${D}${libdir} -ef ${D}${base_libdir} ]; then
		install -d ${D}${libdir}
		mv ${D}${base_libdir}/pkgconfig ${D}${libdir}
		rm ${D}${base_libdir}/libe2p.so
		ln -sf ${base_libdir}/libe2p.so.2 ${D}${libdir}/libe2p.so
		rm ${D}${base_libdir}/libcom_err.so
		ln -sf ${base_libdir}/libcom_err.so.2 ${D}${libdir}/libcom_err.so
		rm ${D}${base_libdir}/libext2fs.so
		ln -sf ${base_libdir}/libext2fs.so.2 ${D}${libdir}/libext2fs.so
		rm ${D}${base_libdir}/libss.so
		ln -sf ${base_libdir}/libss.so.2 ${D}${libdir}/libss.so
		mv ${D}${base_libdir}/*.a ${D}${libdir}
	fi
	#Some files belong to sbindir
	install -d ${D}${sbindir}
	mv ${D}${base_sbindir}/e2freefrag ${D}${sbindir}	
	mv ${D}${base_sbindir}/e4defrag ${D}${sbindir}
	mv ${D}${base_sbindir}/filefrag ${D}${sbindir}
	mv ${D}${base_sbindir}/mklost+found ${D}${sbindir}
	
	oe_multilib_header ext2fs/ext2_types.h			
}

PACKAGES =+ "comerr-dev e2fsck-static e2fslibs e2fslibs-dev libcomerr \
	     libss ss-dev comerr-staticdev e2fslibs-staticdev ss-staticdev"

FILES_comerr-dev = "\
		${bindir}/compile_et \
		${includedir}/com_err.h \
		${includedir}/et/* \
		${libdir}/libcom_err.so \
		${libdir}/pkgconfig/com_err.pc \
		${datadir}/et"
FILES_e2fsck-static = "${sbin}/e2fsck.static"
FILES_comerr-staticdev = "${libdir}/libcom_err.a"
FILES_e2fslibs = "\
		${base_libdir}/libe2p.so.* \
		${base_libdir}/libext2fs.so.*"
FILES_e2fslibs-dev = " \
		${includedir}/e2p/* \
		${includedir}/ext2fs/* \
		${libdir}/libe2p.so \
		${libdir}/libext2fs.so \
		${libdir}/pkgconfig/e2p.pc \
		${libdir}/pkgconfig/ext2fs.pc \
		${infodir}"
FILES_libcomerr = "${base_libdir}/libcom_err.so.*"
FILES_e2fslibs-staticdev = " \
		${libdir}/libext2fs.a \
		${libdir}/libe2p.a"
FILES_${PN} = " \
		${sysconfdir} \
		${base_sbindir}/b* \
		${base_sbindir}/d* \
		${base_sbindir}/e2fsck \
		${base_sbindir}/e2image \
		${base_sbindir}/e2label \
		${base_sbindir}/e2undo \
		${base_sbindir}/fsck* \
		${base_sbindir}/logsave \
		${base_sbindir}/mke2fs* \
		${base_sbindir}/mkfs* \
		${base_sbindir}/resize2fs \
		${base_sbindir}/tune2fs* \
		${bindir}/lsattr \
		${bindir}/chattr \
		${sbindir}/e2freefrag \
		${sbindir}/mklost+found \
		${sbindir}/filefrag \
		${sbindir}/e4defrag \
		" 
FILES_libss = "${base_libdir}/libss.so.*"
FILES_ss-dev = " \
		${bindir}/mk_cmds \
		${includedir}/ss \
		${libdir}/libss.so \
		${libdir}/pkgconfig/ss.pc \
		${datadir}/ss"
FILES_ss-staticdev = "${libdir}/libss.a"

# Correct the package name
DEBIANNAME_libcomerr = "libcomerr2"
DEBIANNAME_libss = "libss2"

ALTERNATIVE_PRIORITY="100"
ALTERNATIVE_${PN} = "mke2fs mkfs.ext2 tune2fs"
ALTERNATIVE_LINK_NAME[mke2fs] = "${base_sbindir}/mke2fs"
ALTERNATIVE_LINK_NAME[mkfs.ext2] = "${base_sbindir}/mkfs.ext2"
ALTERNATIVE_TARGET[mkfs.ext2] = "${base_sbindir}/mkfs.ext2.${DPN}"
ALTERNATIVE_LINK_NAME[tune2fs] = "${base_sbindir}/tune2fs"

BBCLASSEXTEND = "native"
