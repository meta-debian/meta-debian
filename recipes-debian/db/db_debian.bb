require recipes-support/db/db_5.3.21.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-support/db/db:"

inherit debian-package
DEBIAN_SECTION = "libs"
DPN = "db5.3"
DPR = "1"

LICENSE = "BSD"
LIC_FILES_CHKSUM = " \
file://${DEBIAN_UNPACK_DIR}/LICENSE;md5=ed1158e31437f4f87cdd4ab2b8613955 \
"

S = "${DEBIAN_UNPACK_DIR}/dist"
B = "${DEBIAN_UNPACK_DIR}/build_unix"

SRC_URI += " \
file://arm-thumb-mutex_db5.patch;patchdir=.. \                      
file://fix-parallel-build.patch \
"
