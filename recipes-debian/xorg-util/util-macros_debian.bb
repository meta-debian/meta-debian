#
# base recipe: meta/recipes-graphics/xorg-util/util-macros_1.18.0.bb
# base branch: daisy
#

SUMMARY = "X autotools macros"
DESCRIPTION = "M4 autotools macros used by various X.org programs."

require xorg-util-common.inc

PR = "${INC_PR}.0"

S = "${DEBIAN_UNPACK_DIR}/util-macros"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=1970511fddd439b07a6ba789d28ff662"

# ${PN} is empty so we need to tweak -dev and -dbg package dependencies
RDEPENDS_${PN}-dev = ""
RRECOMMENDS_${PN}-dbg = "${PN}-dev (= ${EXTENDPKGV})"

BBCLASSEXTEND = "native nativesdk"

# alway try to apply debian patches by quilt
DEBIAN_PATCH_TYPE = "quilt"

# Fix QA issue file not shipped to any package
FILES_${PN} += "${datadir}"
