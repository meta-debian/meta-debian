#
# Base recipe: meta/recipes-graphics/xorg-lib/libxpm_3.5.11.bb
# Base branch: daisy
#

SUMMARY = "Xpm: X Pixmap extension library"
DESCRIPTION = "libXpm provides support and common operation for the XPM \
pixmap format, which is commonly used in legacy X applications.  XPM is \
an extension of the monochrome XBM bitmap specificied in the X \
protocol."

require xorg-lib-common.inc
PV = "3.5.11"

PR = "${INC_PR}.0"

# libxpm requires xgettext to build
inherit gettext

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=51f4270b012ecd4ab1a164f5f4ed6cf7"
DEPENDS += "libxext libsm libxt"

# Follow package name in Debian
PACKAGES =+ "xpmutils"
FILES_xpmutils = "${bindir}/cxpm ${bindir}/sxpm {datadir}/man/man1"

BBCLASSEXTEND = "native"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
