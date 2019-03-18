#
# base recipe: meta/recipes-core/readline/readline_7.0.bb
# base branch: master
# base commit: 028a292001f64ad86c6b960a05ba1f6fd72199de
#

require recipes-core/readline/readline.inc

inherit debian-package
require recipes-debian/sources/readline.inc

FILESPATH_append = ":${COREBASE}/meta/recipes-core/readline/files:${COREBASE}/meta/recipes-core/readline/readline-8.0"
SRC_URI += "file://inputrc \
            file://configure-fix.patch \
            "
