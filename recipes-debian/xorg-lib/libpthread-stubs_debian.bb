#
# Base recipe: meta/recipes-graphics/xorg-lib/libpthread-stubs_0.3.bb
# Branch: daisy
#

SUMMARY = "Library that provides weak aliases for pthread functions"
DESCRIPTION = "This library provides weak aliases for pthread functions \
not provided in libc or otherwise available by default."
HOMEPAGE = "http://xcb.freedesktop.org"
BUGTRACKER = "http://bugs.freedesktop.org/buglist.cgi?product=XCB"

PR = "r0"

inherit debian-package

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=6edc1fea03d959f0c2d743fe5ca746ad"

# Avoid a parallel build problem
PARALLEL_MAKE = ""

inherit autotools pkgconfig

BBCLASSEXTEND = "native nativesdk"

# There is no Debian's patch
DEBIAN_PATCH_TYPE = "nopatch"

# Correct the package name follow Debian
DEBIANNAME_${PN}-dev = "${PN}0-dev"
