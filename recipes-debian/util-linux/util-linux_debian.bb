require recipes-core/util-linux/util-linux_2.24.1.bb

FILESEXTRAPATHS_prepend = "\
${THISDIR}/files:${COREBASE}/meta/recipes-core/util-linux/util-linux:\
"

inherit debian-package
DEBIAN_SECTION = "base"
DPR = "1"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

# Exclude inappropriate patch which for newer version
# uclibc-__progname-conflict.patch
# fix-configure.patch
# fix-parallel-build.patch
# util-linux-native.patch
# util-linux-native-qsort.patch
# util-linux-ng-replace-siginterrupt.patch
SRC_URI += " \
	file://MCONFIG \
	file://make_include \
	file://swapargs.h \
	file://defines.h \
	file://util-linux-ng-2.16-mount_lock_path.patch \
	file://configure-sbindir.patch \
"

# Fix error when checking needed scanf type modifiers
CACHED_CONFIGUREVARS += "scanf_cv_type_modifier=as"

# Creating folder for holding swapargs.h file
do_compile_prepend() {
	mkdir -p ${S}/mount-deprecated
}

# Fixing QA Issue: package contains symlink .so
INSANE_SKIP_${PN} = "dev-so"

# alway try to apply debian patches by quilt
DEBIAN_PATCH_TYPE = "quilt"

# FIXME: Temporarily hard-code-ly remove flags that version 2.20
# doesnot support to avoid QA warning about unrecognised configure option
EXTRA_OECONF_remove = " \
	--without-systemdsystemunitdir \
	--without-udev \
	--disable-chfn-chsh \
	--disable-runuser \
	--disable-login \
	--disable-setpriv \
	--disable-newgrp \
	--disable-vipw \
	--disable-socket-activation \
	--disable-su \
"

# To fix WARNING at build time: e2fsprogs-native and util-linux-native both
# provide libuuid header and uuidgen utility.
EXTRA_OECONF_class-native += " \
	--disable-libuuid \
"
