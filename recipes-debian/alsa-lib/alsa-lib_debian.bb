require recipes-multimedia/alsa/${PN}_1.0.27.2.bb
FILESEXTRAPATHS_prepend = "${THISDIR}/files:${COREBASE}/meta/recipes-multimedia/alsa/alsa-lib:"

inherit debian-package
DEBIAN_SECTION = "libs"
DPR = "0"

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=7fbc338309ac38fefcd64b04bb903e34"

# make Check-if-wordexp-function-is-supported.patch compatible with
# debian version
#
# exclude Update-iatomic.h-functions-definitions-for-mips.patch
# because atomic_add, atomic_sub, atomic_add_return, atomic_sub_return
# were already removed from iatomic.h
#
# exclude fix-tstamp-declaration.patch because there is no error
# "error: field 'tstamp' has incomplete type" 

SRC_URI += " \
file://Check-if-wordexp-function-is-supported_debian.patch \
"
