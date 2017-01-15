#
# base recipe: meta/recipes-graphics/harfbuzz/harfbuzz_1.0.2.bb
# base branch: jethro
#

SUMMARY = "OpenType text shaping engine"
DESCRIPTION = "HarfBuzz is an implementation of the OpenType Layout engine (aka layout \
engine) and the script-specific logic (aka shaping engine)."
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/HarfBuzz"

PR = "r0"

inherit debian-package
PV = "0.9.35"

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=e021dd6dda6ff1e6b1044002fc662b9b \
    file://src/hb-ucdn/COPYING;md5=994ba0f1295f15b4bda4999a5bbeddef \
"

DEPENDS = "glib-2.0 cairo freetype"

inherit autotools pkgconfig lib_package

# Follow debian/rules, but remove:
#	--enable-gtk-doc: we don't need gtk-doc, so remove it to reduce dependency
#	--enable-introspection: unrecognised options
EXTRA_OECONF = " \
    --with-gobject \
    --disable-silent-rules \
    --enable-static \
"
PACKAGECONFIG ?= "graphite2 icu"
PACKAGECONFIG[graphite2] = "--with-graphite2=yes, --with-graphite2=no, graphite2"
PACKAGECONFIG[icu] = "--with-icu, --without-icu, icu"

PACKAGES =+ "${PN}-gobject ${PN}-icu"

FILES_${PN}-gobject = "${libdir}/libharfbuzz-gobject${SOLIBS}"
FILES_${PN}-icu = "${libdir}/libharfbuzz-icu${SOLIBS}"

DEBIANNAME_${PN} = "lib${PN}0b"

BBCLASSEXTEND = "native"
