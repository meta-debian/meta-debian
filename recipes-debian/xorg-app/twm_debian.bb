require xorg-app-common.inc
#PE = "1"

DESCRIPTION = "tiny window manager"

DEPENDS += " virtual/libx11 libxext libxt libxmu"

ALTERNATIVE_PATH = "${bindir}/twm"
ALTERNATIVE_NAME = "x-window-manager"
ALTERNATIVE_LINK = "${bindir}/x-window-manager"
ALTERNATIVE_PRIORITY = "1"

#
# meta-debian
#
inherit debian-package

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=4c6d42ef60e8166aa26606524c0b9586"
DEBIAN_SECTION = "x11"
DPR = "r0"
DEBIAN_PATCH_TYPE = "quilt"

SRC_URI += "file://twmrc"

do_install_append() {
	install -d ${D}/root/
	install -m 0644 ${WORKDIR}/twmrc ${D}/root/.twmrc
}
FILES_${PN} += " /root/ "

