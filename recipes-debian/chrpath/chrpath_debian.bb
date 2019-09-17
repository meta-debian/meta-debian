#
# base recipe: meta/recipes-devtools/chrpath/chrpath_0.16.bb
# base branch: warrior
#

SUMMARY = "Tool to edit rpath in ELF binaries"
DESCRIPTION = "chrpath allows you to change the rpath (where the \
application looks for libraries) in an application. It does not \
(yet) allow you to add an rpath if there isn't one already."
HOMEPAGE = "http://alioth.debian.org/projects/chrpath/"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

inherit debian-package
require recipes-debian/sources/chrpath.inc

FILESPATH_append = ":${COREBASE}/meta/recipes-devtools/chrpath/chrpath"
SRC_URI += "file://standarddoc.patch"

DEBIAN_PATCH_TYPE = "nopatch"

inherit autotools

# We don't have a staged chrpath-native for ensuring our binary is
# relocatable, so use the one we've just built
CHRPATH_BIN_class-native = "${B}/chrpath"

PROVIDES_append_class-native = " chrpath-replacement-native"
NATIVE_PACKAGE_PATH_SUFFIX = "/${PN}"

BBCLASSEXTEND = "native nativesdk"
