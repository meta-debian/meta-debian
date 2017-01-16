#
# base recipe: meta/recipes-graphics/mesa/libglu_9.0.0.bb
# base branch: jethro
#

SUMMARY = "Mesa OpenGL utility library (GLU)"
DESCRIPTION = "GLU offers simple interfaces for building mipmaps; checking for the \
presence of extensions in the OpenGL (or other libraries which follow \
the same conventions for advertising extensions); drawing \
piecewise-linear curves, NURBS, quadrics and other primitives \
(including, but not limited to, teapots); tesselating surfaces; setting \
up projection matrices and unprojecting screen coordinates to world \
coordinates."

inherit debian-package
PV = "9.0.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
    file://include/GL/glu.h;endline=29;md5=6b79c570f644363b356456e7d44471d9 \
    file://src/libtess/tess.c;endline=29;md5=6b79c570f644363b356456e7d44471d9 \
"

# There is no debian patches
DEBIAN_PATCH_TYPE = "nopatch"

DEPENDS = "virtual/libgl"

inherit autotools distro_features_check pkgconfig

# Requires libGL.so which is provided by mesa when x11 in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "x11"

DEBIANNAME_${PN} = "${PN}1-mesa"
DEBIANNAME_${PN}-dev = "${PN}1-mesa-dev"
RPROVIDES_${PN} += "${PN}-mesa"
RPROVIDES_${PN}-dev += "${PN}-mesa-dev"
