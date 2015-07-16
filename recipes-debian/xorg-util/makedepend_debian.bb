#
# base recipe: meta/recipes-graphics/xorg-util/makedepend_1.0.5.bb
# base branch: daisy
#

SUMMARY = "create dependencies in makefiles"
DESCRIPTION = "The makedepend program reads each sourcefile in sequence \
and parses it like a C-preprocessor, processing \
all #include, #define,  #undef, #ifdef, #ifndef, #endif, #if, #elif \
and #else directives so that it can correctly tell which #include, \
directives would be used in a compilation. Any #include, directives \
can reference files having other #include directives, and parsing will \
occur in these files as well."

require xorg-util-common.inc

PR = "${INC_PR}.0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=43a6eda34b48ee821b3b66f4f753ce4f"

S = "${DEBIAN_UNPACK_DIR}/makedepend"

DEPENDS = "xproto util-macros"

BBCLASSEXTEND = "native"
