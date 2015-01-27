require recipes-multimedia/flac/${BPN}_1.3.0.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-multimedia/flac/flac-1.3.0"

inherit debian-package
DEBIAN_SECTION = "sound"
DPR = "0"

LICENSE = "GFDL-1.2 & GPL-2.0 & LGPL-2.1"
LIC_FILES_CHKSUM = " \
file://COPYING.FDL;md5=ad1419ecc56e060eccf8184a87c4285f \
file://COPYING.GPL;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
file://COPYING.LGPL;md5=fbc093901857fcd118f065f900982c24 \
file://COPYING.Xiph;md5=755582d124a03e3001afea59fc02b61b \
"

SRC_URI += " \
file://0001-Fix-Makefile.am-altivec-logic.patch \
"
