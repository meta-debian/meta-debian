require recipes-kernel/dtc/dtc_git.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-kernel/dtc/dtc:"

DPN = "device-tree-compiler"
inherit debian-package
DEBIAN_SECTION = "devel"
DPR = "0"

LICENSE = "GPL-2.0"
LIC_FILES_CHKSUM = "file://GPL;md5=94d55d512a9ba36caa9b7df079bae19f"

# alway try to apply debian patches by quilt
DEBIAN_PATCH_TYPE = "quilt"

SRC_URI += " \
file://make_install.patch \
"
