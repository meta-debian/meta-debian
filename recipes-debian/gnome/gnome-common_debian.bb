#
# base recipe: meta/recipes-gnome/gnome/gnome-common_3.14.0.bb
# base branch: jethro
#

SUMMARY = "common scripts and macros to develop with GNOME"
DESCRIPTION = "gnome-common is an extension to autoconf, automake and libtool for the GNOME \
environment and GNOME using applications. Included are gnome-autogen.sh and \
several macros to help in GNOME source trees."

PR = "r0"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263"

# debian-package needs be inherited after gnomebase
# to overwrite SRC_URI value.
inherit gnomebase allarch
inherit debian-package
PV = "3.14.0"

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

EXTRA_AUTORECONF = ""
DEPENDS = ""

FILES_${PN} += "${datadir}/aclocal"
FILES_${PN}-dev = ""

RDEPENDS_${PN} += "autoconf automake libtool pkg-config gettext intltool autopoint"
RDEPENDS_${PN}_class-native = ""

BBCLASSEXTEND = "native nativesdk"
