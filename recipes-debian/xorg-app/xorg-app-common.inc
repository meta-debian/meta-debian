#
# Base recipe: recipes-graphics/xorg-app/xorg-app-common.inc
# Base branch: daisy
#

SUMMARY = "X application"
HOMEPAGE = "http://www.x.org/"
BUGTRACKER = "https://bugs.freedesktop.org"

INC_PR = "r0"
LICENSE = "MIT-X"
DEPENDS = "util-macros-native virtual/libx11"

inherit debian-package autotools pkgconfig

FILES_${PN} += " ${libdir}/X11/${BPN} ${datadir}/X11/app-defaults/"
