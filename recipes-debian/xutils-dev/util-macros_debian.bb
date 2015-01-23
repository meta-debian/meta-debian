require recipes-graphics/xorg-util/util-macros_1.18.0.bb

BPN = "xutils-dev"

inherit debian-package
DEBIAN_SECTION = "x11"
DPR = "0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=1970511fddd439b07a6ba789d28ff662"

S = "${DEBIAN_UNPACK_DIR}/util-macros"
# Fix QA issue file not shipped to any package
FILES_${PN} += "${datadir}"
