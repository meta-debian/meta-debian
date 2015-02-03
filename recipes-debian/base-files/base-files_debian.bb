require recipes-core/base-files/${BPN}_3.0.14.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-core/base-files/${BPN}:"

inherit debian-package
DEBIAN_SECTION = "admin"
DPR = "0"

LICENSE = "Apache-2.0 & Artistic-1.0 & BSD \
	& GFDL-1.2 & GFDL-1.3 & GPL-1.0 & GPL-2.0 & GPL-3.0 \
	& LGPL-2.0 & LGPL-2.1 & LGPL-3.0"
LIC_FILES_CHKSUM = " \
file://licenses/Apache-2.0;md5=3b83ef96387f14655fc854ddc3c6bd57 \
file://licenses/BSD;md5=3775480a712fc46a69647678acb234cb \
"

SRC_URI += " \
file://rotation \
file://nsswitch.conf \
file://motd \
file://inputrc \
file://host.conf \
file://profile \
file://shells \
file://fstab \
file://filesystems \
file://issue.net \
file://issue \
file://usbd \
file://share/dot.bashrc \
file://share/dot.profile \
file://licenses/GPL-2 \
"

