#
# Base recipe: meta/recipes-multimedia/libvorbis/libvorbis_1.3.4.bb
# Base branch: daisy
# Base commit: 9e4aad97c3b4395edeb9dc44bfad1092cdf30a47
#

SUMMARY = "Ogg Vorbis Audio Codec"
DESCRIPTION = "Ogg Vorbis is a high-quality lossy audio codec \
that is free of intellectual property restrictions. libvorbis \
is the main vorbis codec library."
HOMEPAGE = "http://www.vorbis.com/"
BUGTRACKER = "https://trac.xiph.org"

inherit debian-package autotools pkgconfig
PV = "1.3.4"

PR = "r0"
DEPENDS = "libogg"

LICENSE = "BSD"
LIC_FILES_CHKSUM = "file://COPYING;md5=ca77c6c3ea4d29cb68dce8ef5ab0d897 \
                    file://include/vorbis/vorbisenc.h;beginline=1;endline=11;md5=d1c1d138863d6315131193d4046d81cb"

# vorbisfile.c reveals a problem in the gcc register spilling for the
# thumb instruction set...
FULL_OPTIMIZATION_thumb = "-O0"

EXTRA_OECONF = "--with-ogg-libraries=${STAGING_LIBDIR} \
                --with-ogg-includes=${STAGING_INCDIR} \
                --enable-static --with-pic"

# Correct dependency according to Debian rules
do_install_append() {
	cd ${D}${libdir}
	sed -i "/dependency_libs/ s/'.*'/''/" `find -name '*.la'`
}

# Add more packages according to Debian packages
PACKAGES += "libvorbisenc libvorbisfile"

# Set list of files in each package accorindg based on Debian
FILES_${PN} = "${libdir}/libvorbis.so.*"
FILES_libvorbisenc = "${libdir}/libvorbisenc.so.*"
FILES_libvorbisfile = "${libdir}/libvorbisfile.so.*"

# Change name of .deb file according to Debian package name
DEBIANNAME_${PN} = "libvorbis0a"
