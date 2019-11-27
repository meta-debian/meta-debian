require ${COREBASE}/meta/recipes-devtools/pseudo/pseudo.inc

LICENSE = "LGPLv2.1"
LIC_FILES_CHKSUM = "file://COPYING;md5=243b725d71bb5df4a1e5920b344b86ad"

inherit debian-package
require recipes-debian/sources/pseudo.inc
FILESPATH_append = ":${COREBASE}/meta/recipes-devtools/pseudo/files"

SRC_URI += " \
           file://0001-configure-Prune-PIE-flags.patch \
           file://fallback-passwd \
           file://fallback-group \
           file://moreretries.patch \
           file://toomanyfiles.patch \
           "
