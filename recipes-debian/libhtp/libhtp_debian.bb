# base recipe: meta-security/recipes-security/suricata/libhtp_0.5.27.bb
# base branch: warrior

SUMMARY = "LibHTP is a security-aware parser for the HTTP protocol and the related bits and pieces."

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=61b10667ba85f7d893be233d4d1d1c6a"

inherit debian-package
require recipes-debian/sources/libhtp.inc

DEBIAN_QUILT_PATCHES = ""

DEPENDS = "zlib"

inherit autotools pkgconfig

CFLAGS += "-D_DEFAULT_SOURCE"
