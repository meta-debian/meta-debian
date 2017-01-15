#
# base recipe: meta/recipes-devtools/desktop-file-utils/desktop-file-utils-native_0.22.bb
# base branch: daisy
#

PR = "r0"

inherit debian-package
PV = "0.22"

SECTION = "console/utils"
SUMMARY = "Command line utilities for working with *.desktop files"
HOMEPAGE = "http://www.freedesktop.org/wiki/Software/desktop-file-utils"
LICENSE = "GPLv2"

LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"
DEPENDS = "glib-2.0-native"

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

inherit autotools native
