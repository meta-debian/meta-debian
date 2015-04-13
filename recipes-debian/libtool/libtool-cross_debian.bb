require recipes-devtools/libtool/libtool-cross_2.4.2.bb
FILESEXTRAPATHS_prepend = "\
${THISDIR}/files:${COREBASE}/meta/recipes-devtools/libtool/libtool:\
"

inherit debian-package
DEBIAN_SECTION = "devel"
DPR = "0"

LICENSE = "GPLv2 & LGPLv2.1"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=751419260aa954499f7abaabaa882bbe\
	file://libltdl/COPYING.LIB;md5=e3eda01d9815f8d24aae2dbd89b68b06\
"

# Patch file fixinstall.patch and patch file
# dont-depend-on-help2man.patch can not apply, so
# fixinstall_edited.patch and 
# dont-depend-on-help2man-edited.patch was created for apply
# with the same purpose.
SRC_URI += " \
file://trailingslash.patch \
file://rename-with-sysroot.patch \
file://use-sysroot-in-libpath.patch \
file://fix-final-rpath.patch \
file://avoid_absolute_paths_for_general_utils.patch \
file://fix-rpath.patch \
file://respect-fstack-protector.patch \
file://norm-rpath.patch \
file://dont-depend-on-help2man-edited.patch \
file://fix-resolve-lt-sysroot.patch \
file://prefix.patch \
file://fixinstall.patch \
"

# Don't apply debian/patches/link_all_deplibs.patch
# This patch make libtool do not link all dependency libs when create shared 
# object archive and causes an error while link gettext's libraries although 
# the dependency flag was already added.

do_debian_patch_prepend() {
	sed -i -e "/link_all_deplibs/ d" ${S}/debian/patches/series
}
