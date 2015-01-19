require recipes-core/netbase/netbase_5.2.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-core/netbase/netbase:"

inherit debian-package

DEBIAN_SECTION = "admin"
DPR = "0"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=3dd6192d306f582dee7687da3d8748ab"

SRC_URI += " \
file://hosts \
"
