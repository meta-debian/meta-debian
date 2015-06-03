HOMEPAGE = "http://www.x.org"
SECTION = "x11/data"
LICENSE = "MIT-X"

XORG_PN = "${PN}"
INC_PR = "r2"

#SRC_URI = "${XORG_MIRROR}/individual/data/${XORG_PN}-${PV}.tar.bz2;name=archive"
#S = "${WORKDIR}/${XORG_PN}-${PV}"

inherit autotools pkgconfig
#
# Meta-debian
#
inherit debian-package
