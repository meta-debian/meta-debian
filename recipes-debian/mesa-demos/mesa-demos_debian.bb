#
# base recipes: meta/recipes-graphics/mesa/mesa-demos_8.2.0.bb
# base branch: jethro
#

SUMMARY = "Miscellaneous Mesa GL utilities"
DESCRIPTION = "This package provides several basic GL utilities built by Mesa"
HOMEPAGE = "http://mesa3d.org/"

inherit debian-package
PV = "8.2.0"

LICENSE = "MIT-style & PD & LGPLv2+"
LIC_FILES_CHKSUM = " \
    file://src/xdemos/glxinfo.c;endline=20;md5=528e51cb8d683b1271806b7dce9b28d3 \
    file://src/xdemos/glxdemo.c;beginline=3;endline=8;md5=5c753761467aa6d4fb9f067671715e3e \
    file://src/util/glstate.h;beginline=2;endline=19;md5=ca7dfb81015ae8cf1a0c9e019309571c \
"

DEBIAN_PATCH_TYPE = "quilt"

DEPENDS = "glew virtual/libgl"

inherit autotools pkgconfig

PACKAGECONFIG ??= "drm freetype2 egl gles1 gles2 x11"
PACKAGECONFIG[drm] = "--enable-libdrm,--disable-libdrm,libdrm"
PACKAGECONFIG[egl] = "--enable-egl,--disable-egl,virtual/egl"
PACKAGECONFIG[freetype2] = "--enable-freetype2,--disable-freetype2,freetype"
PACKAGECONFIG[gbm] = "--enable-gbm,--disable-gbm,virtual/libgl"
PACKAGECONFIG[gles1] = "--enable-gles1,--disable-gles1,virtual/libgles1"
PACKAGECONFIG[gles2] = "--enable-gles2,--disable-gles2,virtual/libgles2"
PACKAGECONFIG[glut] = "--with-glut=${STAGING_EXECPREFIXDIR},--without-glut,"
PACKAGECONFIG[osmesa] = "--enable-osmesa,--disable-osmesa,"
PACKAGECONFIG[vg] = "--enable-vg,--disable-vg,virtual/libopenvg"
PACKAGECONFIG[wayland] = "--enable-wayland,--disable-wayland,virtual/libgl wayland"
PACKAGECONFIG[x11] = "--enable-x11,--disable-x11,virtual/libx11"

do_install_append() {
	ln -s es2gears_x11 ${D}${bindir}/es2gears
}

PACKAGES =+ "mesa-utils mesa-utils-extra"

FILES_mesa-utils = " \
    ${bindir}/glxdemo \
    ${bindir}/glxgears \
    ${bindir}/glxheads \
    ${bindir}/glxinfo \
"
FILES_mesa-utils-extra = "${bindir}/es2*"
