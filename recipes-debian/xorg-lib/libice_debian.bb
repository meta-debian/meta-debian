SUMMARY = "ICE: Inter-Client Exchange library"

DESCRIPTION = "The Inter-Client Exchange (ICE) protocol provides a \
generic framework for building protocols on top of reliable, byte-stream \
transport connections. It provides basic mechanisms for setting up and \
shutting down connections, for performing authentication, for \
negotiating versions, and for reporting errors. "

require ${COREBASE}/meta/recipes-graphics/xorg-lib/xorg-lib-common.inc

inherit debian-package
require recipes-debian/sources/libice.inc
DEBIAN_PATCH_TYPE = "nopatch"
DEBIAN_UNPACK_DIR = "${WORKDIR}/${XORG_PN}-${PV}"
FILESPATH_append = ":${COREBASE}/meta/recipes-graphics/xorg-lib/libice"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=d162b1b3c6fa812da9d804dcf8584a93"

DEPENDS += "xorgproto xtrans"
PROVIDES = "ice"

XORG_PN = "libICE"

BBCLASSEXTEND = "native"

SRC_URI += "file://CVE-2017-2626.patch"

PACKAGECONFIG ??= "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"
PACKAGECONFIG[arc4] = "ac_cv_lib_bsd_arc4random_buf=yes,ac_cv_lib_bsd_arc4random_buf=no,libbsd"
PACKAGECONFIG[ipv6] = "--enable-ipv6,--disable-ipv6,"
