SUMMARY = "Data files for the imake utility"
DESCRIPTION = "The xorg-cf-files package contains the data files for the imake utility, \
defining the known settings for a wide variety of platforms (many of which \
have not been verified or tested in over a decade), and for many of the \
libraries formerly delivered in the X.Org monolithic releases."

require xorg-util-common.inc

PR = "${INC_PR}.0"

LIC_FILES_CHKSUM = "file://COPYING;md5=0f334a06f2de517e37e86d6757167d88"

DEPENDS = "util-macros font-util"

FILES_${PN} += "${libdir}/X11/config/*"
