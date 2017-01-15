#
# base recipe: meta/recipes-support/atk/atk_2.10.0.bb
# base branch: daisy
#

PR = "r0"

DPN = "atk1.0"

LICENSE = "GPLv2+ & LGPLv2+"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7 \
	file://atk/atkutil.c;endline=18;md5=6fd31cd2fdc9b30f619ca8d819bc12d3 \
	file://atk/atk.h;endline=18;md5=fcd7710187e0eae485e356c30d1b0c3b \
"

DEPENDS = "glib-2.0"

# debian-package must be inherit after gnomebase to overwrite SRC_URI
inherit gnomebase gtk-doc
inherit debian-package
PV = "2.14.0"

GNOME_COMPRESS_TYPE = "xz"

EXTRA_OECONF = "--disable-glibtest \
                --disable-introspection"

DEBIANNAME_${PN} = "libatk1.0-0"
DEBIANNAME_${PN}-dev = "libatk1.0-dev"
DEBIANNAME_${PN}-doc = "libatk1.0-doc"
DEBIANNAME_${PN}-dbg = "libatk1.0-dbg"

BBCLASSEXTEND = "native"
