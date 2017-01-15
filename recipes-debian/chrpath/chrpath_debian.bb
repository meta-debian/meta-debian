#
# base recipe: meta/recipes-devtools/chrpath/chrpath_0.16.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "0.16"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=59530bdf33659b29e73d4adb9f9f6552"

SRC_URI += " \
file://standarddoc.patch \
"

inherit autotools

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"

# We don't have a staged chrpath-native for ensuring our binary is
# relocatable, so use the one we've just built
CHRPATH_BIN_class-native = "${B}/chrpath"

PROVIDES_append_class-native = " chrpath-replacement-native"
NATIVE_PACKAGE_PATH_SUFFIX = "/${PN}"

BBCLASSEXTEND = "native nativesdk"
