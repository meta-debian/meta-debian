#
# Base recipe: meta/recipes-graphics/xorg-lib/libfontenc_1.1.2.bb
# Base branch: daisy
# 

SUMMARY = "X font encoding library"

DESCRIPTION = "libfontenc is a library which helps font libraries \
portably determine and deal with different encodings of fonts."

require xorg-lib-common.inc
PV = "1.1.2"

PR = "${INC_PR}.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=96254c20ab81c63e65b26f0dbcd4a1c1"

DEPENDS += "zlib xproto font-util"

BBCLASSEXTEND = "native"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"

# Correct the package name follow Debian
DEBIANNAME_${PN}-dbg = "${PN}1-dbg"
