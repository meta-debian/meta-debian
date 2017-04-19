#
# Base recipe: meta/recipes-graphics/xorg-lib/libxinerama_1.1.3.bb
# Base branch: daisy
#

require xorg-lib-common.inc
PV = "1.1.3"

SUMMARY = "Xinerama: Xinerama extension library"

DESCRIPTION = "Xinerama is a simple library designed to interface the \
Xinerama Extension for retrieving information about physical output \
devices which may be combined into a single logical X screen."

PR = "${INC_PR}.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=6f4f634d1643a2e638bba3fcd19c2536 \
file://src/Xinerama.c;beginline=2;endline=25;md5=fcef273bfb66339256411dd06ea79c02"

DEPENDS += "libxext xineramaproto"
PROVIDES = "xinerama"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"

# Correct the package name follow Debian
DEBIANNAME_${PN} = "${PN}1"
DEBIANNAME_${PN}-dbg = "${PN}1-dbg"
