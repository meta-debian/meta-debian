#
# Base recipe: meta/recipes-core/ncurses/ncurses_6.1.bb
# Base branch: master
# Base commit: a5d1288804e517dee113cb9302149541f825d316
# 

require recipes-core/ncurses/ncurses.inc

inherit debian-package
PV = "6.1+20180210"
DPR = "-4"
DSC_URI = "${DEBIAN_MIRROR}/main/n/${BPN}/${BPN}_${PV}${DPR}.dsc;md5sum=7136695c6d8f985dd8de71dcc77e88a0"
DEBIAN_UNPACK_DIR = "${WORKDIR}/${BPN}-6.1-20180210"

EXTRA_OECONF += "--with-abi-version=5"
