require recipes-core/glib-2.0/glib-2.0_2.38.2.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-core/glib-2.0/glib-2.0:\
${COREBASE}/meta/recipes-core/glib-2.0/files:\
"

inherit debian-package
DEBIAN_SECTION = "libs"

DPR = "0"
DPN = "glib2.0"

LICENSE = "LGPLv2 & PD"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=3bf50002aefd002f49e7bb854063f7e7\
	file://docs/reference/COPYING;md5=f51a5100c17af6bae00735cd791e1fcc\
"


#
# Patch files:
# 0001-gio-Fix-Werror-format-string-errors-from-mismatched-.patch,
# ptest-dbus.patch, gtest-skip-fixes.patch, gio-test-race.patch 
# in reused recipe was not included in SRC_URI since it was 
# applied already in latest supported version of glib-2.0.
#
SRC_URI += "\
 file://configure-libtool.patch \
 file://fix-conflicting-rand.patch \
 file://add-march-i486-into-CFLAGS-automatically.patch \
 file://glib-2.0-configure-readlink.patch \
 file://run-ptest \
 file://ptest-paths.patch \
 file://uclibc.patch \
"
