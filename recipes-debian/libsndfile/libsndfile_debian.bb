SUMMARY = "Library for reading/writing audio files"
DESCRIPTION = "libsndfile is a library of C routines for reading and writing files containing \
 sampled audio data."
HOMEPAGE = "http://www.mega-nerd.com/libsndfile/"

inherit debian-package
PV = "1.0.25"

LICENSE = "LGPLv2.1+ & BSD-3-Clause"
LIC_FILES_CHKSUM = "\
	file://COPYING;md5=e77fe93202736b47c07035910f47974a \
	file://programs/sndfile-cmp.c;endline=32;md5=641a2cf179542b99eb03abaeea248df1"
inherit autotools pkgconfig

DEPENDS += "alsa-lib libvorbis flac"
do_install_append() {
	rm -rf ${D}${bindir}/sndfile-regtest \
	       ${D}${bindir}/sndfile-salvage \
	       ${D}${libdir}/*.la
}

PACKAGES =+ "sndfile-programs"
FILES_sndfile-programs = "${bindir}/*"

PKG_${PN}-dev = "${PN}1-dev"
RPROVIDES_${PN}-dev += "${PN}1-dev"
RPROVIDES_${PN} += "${PN}1"
