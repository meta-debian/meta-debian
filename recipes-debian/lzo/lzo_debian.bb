require recipes-support/lzo/lzo_2.06.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-support/lzo/lzo-2.06:"

inherit debian-package
DEBIAN_SECTION = "libs"
DPR = "0"
DPN = "lzo2"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

SRC_URI += " \
	file://acinclude.m4 \
"
