#
# base recipe: meta/recipes-graphics/xorg-lib/libxkbcommon_0.5.0.bb
# base branch: jethro
#

SUMMARY = "library interface to the XKB compiler"
DESCRIPTION = "xkbcommon is a library to handle keyboard descriptions, including loading them \
from disk, parsing them and handling their state. It's mainly meant for client \
toolkits, window systems, and other system applications; currently that \
includes Wayland, kmscon, GTK+, Clutter, and more."
HOMEPAGE = "http://www.xkbcommon.org/"

inherit debian-package
PV = "0.4.3"

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=9c0b824e72a22f9d2c40b9c93b1f0ddc"

DEPENDS = "util-macros flex-native bison-native"

DEBIAN_PATCH_TYPE = "quilt"

inherit autotools pkgconfig

EXTRA_OECONF = "--with-xkb-config-root=${datadir}/X11/xkb"

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'x11', 'x11', '', d)}"
PACKAGECONFIG[x11] = "--enable-x11,--disable-x11,libxcb xkeyboard-config,"

PACKAGES =+ "${@bb.utils.contains('DISTRO_FEATURES', 'x11', '${PN}-x11 ${PN}-x11-dev', '', d)}"

FILES_${PN}-x11 = "${libdir}/libxkbcommon-x11${SOLIBS}"
FILES_${PN}-x11-dev = " \
    ${includedir}/xkbcommon/xkbcommon-x11.h \
    ${libdir}/libxkbcommon-x11.la \
    ${libdir}/libxkbcommon-x11${SOLIBSDEV} \
    ${libdir}/pkgconfig/xkbcommon-x11.pc \
"
