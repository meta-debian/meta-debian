require recipes-connectivity/nfs-utils/nfs-utils_1.2.9.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-connectivity/nfs-utils/nfs-utils:"

inherit debian-package

DEBIAN_SECTION = "net"
DPR = "0"

LICENSE = "GPLv2+ & MIT & BSD"
LIC_FILES_CHKSUM = "\
file://COPYING;md5=95f3a93a5c3c7888de623b46ea085a84 \
file://install-sh;beginline=10;endline=32;md5=b305c58d8bbd3e6dec5f21cd86edec25 \
file://utils/idmapd/nfs_idmap.h;beginline=6;endline=34;md5=e458358e2a6d47eb6ab729685754a1b1 \
"

SRC_URI += " \
file://0001-configure-Allow-to-explicitly-disable-nfsidmap.patch \
file://nfs-utils-1.0.6-uclibc.patch \
file://nfs-utils-1.2.3-sm-notify-res_init.patch \
file://nfsserver \
file://nfscommon \
file://nfs-utils.conf \
file://nfs-server.service \
file://nfs-mountd.service \
file://nfs-statd.service \
"

