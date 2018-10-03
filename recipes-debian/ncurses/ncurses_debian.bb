#
# Base recipe: meta/recipes-core/ncurses/ncurses_6.1.bb
# Base branch: master
# Base commit: a5d1288804e517dee113cb9302149541f825d316
# 

require recipes-core/ncurses/ncurses.inc

inherit debian-package
require recipes-debian/sources/ncurses.inc
DEBIAN_UNPACK_DIR = "${WORKDIR}/${BPN}-${@d.getVar('PV', True).replace('+','-')}"

EXTRA_OECONF += "--with-abi-version=5"
