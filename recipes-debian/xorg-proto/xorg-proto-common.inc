#
# Base recipe: meta/recipes-graphics/xorg-proto/xorg-proto-common.inc 
# Base branch: daisy
#

SUMMARY = "X protocol headers: ${XORG_PN}"
HOMEPAGE = "http://www.x.org"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=xorg"

INC_PR = "r0"

inherit debian-package

SECTION = "x11/libs"
LICENSE = "MIT-X"

DEPENDS = "util-macros"

inherit autotools pkgconfig
EXTRA_OECONF = "--without-fop --without-xmlto"

UNKNOWN_CONFIGURE_WHITELIST += "--without-fop --without-xmlto"

# ${PN} is empty so we need to tweak -dev and -dbg package dependencies
RDEPENDS_${PN}-dev = ""
RRECOMMENDS_${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"

# On Debian, x11proto-* source packages provide x11proto-*-dev packages
# which are equal to ${PN}-dev on Poky,
# so provide ${DPN}-dev from ${PN}-dev to keep compatible between Poky and Debian.
DEBIANNAME_${PN}-dev = "${DPN}-dev"
RPROVIDES_${PN}-dev = "${DPN}-dev"
