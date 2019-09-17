require recipes-devtools/meson/meson.inc

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://COPYING;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit debian-package
require recipes-debian/sources/meson.inc

FILESPATH_append = ":${COREBASE}/meta/recipes-devtools/meson/meson"
SRC_URI += " \
           file://0001-gtkdoc-fix-issues-that-arise-when-cross-compiling.patch \
           file://0002-gobject-introspection-determine-g-ir-scanner-and-g-i.patch \
           file://0001-Linker-rules-move-cross_args-in-front-of-output_args.patch \
           file://0003-native_bindir.patch \
           file://0001-python-module-do-not-manipulate-the-environment-when.patch \
           file://disable-rpath-handling.patch \
           file://many-cross.patch \
           file://cross-libdir.patch \
           "
