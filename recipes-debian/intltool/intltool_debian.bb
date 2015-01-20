require recipes-devtools/intltool/intltool_0.50.2.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-devtools/intltool/intltool-0.50.2:\
${COREBASE}/meta/recipes-devtools/intltool/files:\
"

inherit debian-package
DEBIAN_SECTION = "devel"

DPR = "0"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

SRC_URI += "\
 file://intltool-nowarn.patch \
 file://uclibc.patch \
 ${NATIVEPATCHES} \
"
# Pass through since there is no patch file in debian/patch/series
debian_patch_quilt() {
	:
}
