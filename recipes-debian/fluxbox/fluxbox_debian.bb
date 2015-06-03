DESCRIPTION="fluxbox Window Manager"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=e90c7c0bee6fc368be0ba779e0eac053"

DEPENDS = "virtual/libx11"

inherit autotools pkgconfig
S = "${WORKDIR}/git"
B = "${S}"

#
# debian
#
inherit debian-package
DEBIAN_SECTION = "x11"
DPR = "0"
