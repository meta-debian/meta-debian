#
# base recipe: meta/recipes-extended/parted/parted_3.2.bb
# base branch: jethro
#

include parted.inc
PR = "${INC_PR}.0"

# fix compile failure while --disable-device-mapper
SRC_URI += "file://fix-compile-failure-while-dis.patch"

DEPENDS = "ncurses-native readline-native util-linux-native"

inherit native

# Disable unneeded features on native to reduce number of dependencies
EXTRA_OECONF += " \
    --disable-device-mapper \
"
