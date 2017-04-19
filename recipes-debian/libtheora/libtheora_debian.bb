#
# Base recipe: meta/recipes-multimedia/libtheora/libtheora_1.1.1.bb
# Base branch: daisy
# Base commit: 9e4aad97c3b4395edeb9dc44bfad1092cdf30a47
#
SUMMARY = "Theora Video Codec"
DESCRIPTION = "The libtheora reference implementation provides the standard encoder and decoder under a BSD license."
HOMEPAGE = "http://xiph.org/"
BUGTRACKER = "https://trac.xiph.org/newticket"

inherit autotools pkgconfig debian-package
PV = "1.1.1+dfsg.1"
PR = "r0"
DEPENDS = "libogg"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=cf91718f59eb6a83d06dc7bcaf411132"

# libtheora-bin contains examples only so no need for it
EXTRA_OECONF = "--disable-examples"

# Specify scope of apply Debian name
LEAD_SONAME = "libtheora.so.0"

DEBIANNAME_${PN} = "libtheora0"
