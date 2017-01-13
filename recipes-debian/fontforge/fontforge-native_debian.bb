SUMMARY = "font editor"
DESCRIPTION = "Besides being a font editor, FontForge is also a font format \
converter, and can convert among PostScript (ASCII & binary Type 1, \
some Type 3s, some Type 0s), TrueType, and OpenType (Type2), \
CID-keyed, SVG, CFF and multiple-master fonts."
HOMEPAGE = "http://fontforge.sourceforge.net/"

PR = "r1"

inherit debian-package
PV = "20120731.b"

LICENSE = "BSD-3-Clause & GPLv2"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=720ee0a73f821888ee48f5d239cf6d73 \
"

DEPENDS = "cairo-native freetype-native giflib-native libjpeg-turbo-native \
           libspiro-native libtool-native libxml2-native pango-native tiff-native"

# python_c-missing-semicolon.patch:
# 	Add missing semicolon in fontforge/python.c.
# check-libxml2-headers-by-xml2-config.patch:
# 	Use xml2-config to check libxml2 headers instead of looking from host system.
SRC_URI += " \
    file://python_c-missing-semicolon.patch \
    file://check-libxml2-headers-by-xml2-config.patch \
"

inherit autotools-brokensep pkgconfig pythonnative native

# Follow debian/rules
EXTRA_OECONF = "--with-regular-link \
                --enable-devicetables \
                --enable-type3 \
                --with-freetype-src=${S}/freetype \
                --with-freetype-bytecode \
                --enable-pyextension \
                "

# We don't need X on native
EXTRA_OECONF_append = " --without-x"

#export some variable from poky, to use for python command
export HOST_SYS
export BUILD_SYS
export STAGING_INCDIR
export STAGING_LIBDIR
