SUMMARY = "Extended cryptographic library (from glibc)"
DESCRIPTION = "Forked code from glibc libary to extract only crypto part."
HOMEPAGE = "https://github.com/besser82/libxcrypt"
SECTION = "libs"
LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM ?= "file://LICENSING;md5=cb3ca4cabd2447a37bf186fad6f79852 \
      file://COPYING.LIB;md5=4fbd65380cdd255951079008b364516c \
"

inherit debian-package
require recipes-debian/sources/libxcrypt.inc
DEBIAN_PATCH_TYPE = "nopatch"
# source format is 3.0 (quilt) but there is no debian/patches
DEBIAN_QUILT_PATCHES = ""

inherit autotools pkgconfig

PROVIDES = "virtual/crypt"

FILES_${PN} = "${libdir}/libcrypt*.so.* ${libdir}/libcrypt-*.so ${libdir}/libowcrypt*.so.* ${libdir}/libowcrypt-*.so"

BUILD_CPPFLAGS = "-I${STAGING_INCDIR_NATIVE} -std=gnu99"
TARGET_CPPFLAGS = "-I${STAGING_DIR_TARGET}${includedir}"

BBCLASSEXTEND = "nativesdk"
