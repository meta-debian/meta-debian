require file-5.16.inc 
FILESEXTRAPATHS_prepend = "${THISDIR}/files:${COREBASE}/meta/recipes-devtools/file/${BPN}:"

inherit debian-package
DEBIAN_SECTION = "utils"
DPR = "0"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://COPYING;md5=975f248ba2aad6c08f97d927adf001c4"
