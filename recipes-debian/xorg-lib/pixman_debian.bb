#
# Base recipe: meta/recipes-graphics/xorg-lib/pixman_0.32.4.bb
# Base branch: daisy
#

SUMMARY = "Pixman: Pixel Manipulation library"

DESCRIPTION = "Pixman provides a library for manipulating pixel regions \
-- a set of Y-X banded rectangles, image compositing using the \
Porter/Duff model and implicit mask generation for geometric primitives \
including trapezoids, triangles, and rectangles."

require xorg-lib-common.inc

PR = "${INC_PR}.0"

inherit debian-package
PV = "0.32.6"

LICENSE = "MIT & MIT-style & PD"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=14096c769ae0cbb5fcb94ec468be11b3 \
file://pixman/pixman-matrix.c;endline=25;md5=ba6e8769bfaaee2c41698755af04c4be \
file://pixman/pixman-arm-neon-asm.h;endline=24;md5=9a9cc1e51abbf1da58f4d9528ec9d49b \
"

DEPENDS += "zlib libpng"
BBCLASSEXTEND = "native nativesdk"

IWMMXT = "--disable-arm-iwmmxt"
LOONGSON_MMI = "--disable-loongson-mmi"
NEON = " --disable-arm-neon "
NEON_class-nativesdk = " --disable-arm-neon "
NEON_armv7a = " "
NEON_armv7a-vfp-neon = " "

EXTRA_OECONF = "--disable-gtk ${IWMMXT} ${LOONGSON_MMI} ${NEON}"
EXTRA_OECONF_class-native = "--disable-gtk"

REQUIRED_DISTRO_FEATURES = ""

#Apply debian patch by quilt
DEBIAN_PATCH_TYPE = "quilt"

DEBIANNAME_${PN}-dbg = "lib${PN}-1-0-dbg"

RPROVIDES_${PN} += "libpixman"
RPROVIDES_${PN}-dev += "libpixman-dev"

LEAD_SONAME = "libpixman*.so"
