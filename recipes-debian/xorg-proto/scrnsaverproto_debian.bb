#
# Base recipe: meta/recipes-graphics/xorg-proto/scrnsaverproto_1.2.2.bb
# Base branch: daisy
#

require xorg-proto-common.inc
PV = "1.2.2"

SUMMARY = "Xscrnsaver: X Screen Saver extension headers"

DESCRIPTION = "This package provides the wire protocol for the X Screen \
Saver extension.  This extension allows an external \"screen saver\" \
client to detect when the alternative image is to be displayed and to \
provide the graphics."

PR = "${INC_PR}.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "\
file://COPYING;md5=eed49b78b15b436c933b6b8b054e3901 \
file://saverproto.h;endline=26;md5=a84c0637305159f3c0ab173aaeede48d"

EXTRA_OECONF_append = " --enable-specs=no"

DPN = "x11proto-scrnsaver"

#There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
