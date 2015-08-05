#
# Base recipe: meta/recipes-graphics/xorg-lib/xorg-lib-common.inc 
# Base branch: daisy
#

HOMEPAGE = "http://www.x.org"
BUGTRACKER = "https://bugs.freedesktop.org/enter_bug.cgi?product=xorg"

INC_PR =  "r0"

inherit debian-package

LICENSE = "MIT-X"
DEPENDS = "util-macros"

inherit autotools distro_features_check pkgconfig

EXTRA_OECONF = "--disable-specs --without-groff --without-ps2pdf --without-fop --without-xmlto"

UNKNOWN_CONFIGURE_WHITELIST += "--enable-malloc0returnsnull --disable-malloc0returnsnull \
                                --disable-specs --without-groff --without-ps2pdf --without-fop --without-xmlto"

REQUIRED_DISTRO_FEATURES ?= "x11"
