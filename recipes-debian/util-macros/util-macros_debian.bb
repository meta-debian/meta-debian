SUMMARY = "X autotools macros"
DESCRIPTION = "M4 autotools macros used by various X.org program."

require recipes-graphics/xorg-util/xorg-util-common.inc

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=1970511fddd439b07a6ba789d28ff662"

inherit debian-package
require recipes-debian/sources/xutils-dev.inc

DEBIAN_PATCH_TYPE = "quilt"

DEBIAN_UNPACK_DIR = "${WORKDIR}/xutils-dev-${PV}"
S = "${DEBIAN_UNPACK_DIR}/util-macros"
BBCLASSEXTEND = "native nativesdk"
