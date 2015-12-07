#
# Base recipe: recipes-graphics/xorg-app/xorg-app-common.inc
# Base branch: daisy
#

SUMMARY = "X application"
HOMEPAGE = "http://www.x.org/"
BUGTRACKER = "https://bugs.freedesktop.org"

INC_PR = "r1"
LICENSE = "MIT-X"
DEPENDS = "util-macros-native virtual/libx11"

# depends on virtual/libx11
REQUIRED_DISTRO_FEATURES = "x11"

inherit debian-package
inherit autotools pkgconfig distro_features_check

FILES_${PN} += " ${libdir}/X11/${BPN} ${datadir}/X11/app-defaults/"
