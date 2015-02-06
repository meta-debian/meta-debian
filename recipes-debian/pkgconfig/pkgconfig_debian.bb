require recipes-devtools/pkgconfig/pkgconfig_0.28.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-devtools/pkgconfig/pkgconfig-0.28:\
${COREBASE}/meta/recipes-devtools/pkgconfig/pkgconfig:\
"

DPN = "pkg-config"
inherit debian-package

DEBIAN_SECTION = "devel"
DPR = "0"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI += " \
file://pkg-config-native.in \
file://fix-glib-configure-libtool-usage.patch \
file://obsolete_automake_macros.patch \
"

# no patch related-rule
DEBIAN_PATCH_TYPE = "nopatch"
