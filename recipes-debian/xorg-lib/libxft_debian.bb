#
# Base recipe: meta/recipes-graphics/xorg-lib/libxft_2.3.1.bb
# Bare branch: daisy
#

SUMMARY = "XFt: X FreeType libary"

DESCRIPTION = "Xft was designed to provide good support for scalable \
fonts, and to do so efficiently.  Unlike the core fonts system, it \
supports features such as anti-aliasing and sub-pixel rasterisation. \
Perhaps more importantly, it gives applications full control over the \
way glyphs are rendered, making fine typesetting and WYSIWIG display \
possible. Finally, it allows applications to use fonts that are not \
installed system-wide for displaying documents with embedded fonts.  Xft \
is not compatible with the core fonts system: usage of Xft requires \
fairly extensive changes to toolkits (user-interface libraries)."

require xorg-lib-common.inc
PV = "2.3.2"

PR = "${INC_PR}.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=90b90b60eb30f65fc9c2673d7cf59e24"

DEPENDS += "virtual/libx11 libxrender freetype fontconfig"
PROVIDES = "xft"

BBCLASSEXTEND = "native"

python () {
        if d.getVar('DEBIAN_NAMES', True):
            d.setVar('PKG_${PN}', '${MLPREFIX}libxft2')
}

DPN = "xft"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"

# Correct the package name follow Debian
DEBIANNAME_${PN}-dbg = "${PN}2-dbg"
