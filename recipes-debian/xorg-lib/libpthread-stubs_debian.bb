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
PV = "0.3"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=6edc1fea03d959f0c2d743fe5ca746ad"

# Avoid a parallel build problem
PARALLEL_MAKE = ""

inherit autotools pkgconfig

BBCLASSEXTEND = "native nativesdk"

# There is no Debian's patch
DEBIAN_PATCH_TYPE = "nopatch"

# Create package follow Debian.
PACKAGES = "${PN}0-dev"

FILES_${PN}0-dev = "${libdir}"
