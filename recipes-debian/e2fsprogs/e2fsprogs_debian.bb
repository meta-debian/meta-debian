require recipes-devtools/e2fsprogs/${PN}_1.42.9.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-devtools/e2fsprogs/e2fsprogs:\
"

inherit debian-package
DEBIAN_SECTION = "admin"
DPR = "1"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=b48f21d765b875bd10400975d12c1ca2"

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

# Automake has dropped such macro, use "mkdir -p" instead
do_configure_prepend() {
	sed -i -e "s:AM_MKINSTALLDIRS:AM_PROG_MKDIR_P:" ${S}/configure.in
	sed -i -e "s:MKINSTALLDIRS = .*:MKINSTALLDIRS = @MKDIR_P@:" \
							${S}/MCONFIG.in
}

# Remove option to disable libuuid to avoid error external uuid library
# not found. 
EXTRA_OECONF_remove = "--disable-libuuid --disable-uuidd"
