require recipes-devtools/quilt/quilt.inc

inherit debian-package
require recipes-debian/sources/quilt.inc

FILESPATH_append = ":${COREBASE}/meta/recipes-devtools/quilt/quilt"
SRC_URI += " \
        file://run-ptest \
        file://Makefile \
        file://test.sh \
        file://0001-tests-Allow-different-output-from-mv.patch \
"
