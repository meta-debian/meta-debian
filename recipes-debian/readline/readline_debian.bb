#
# base recipe: meta/recipes-core/readline/readline_7.0.bb
# base branch: master
# base commit: 028a292001f64ad86c6b960a05ba1f6fd72199de
#

require recipes-core/readline/readline.inc

inherit debian-package
PV = "7.0"
DPR = "-5"
DSC_URI = "${DEBIAN_MIRROR}/main/r/${BPN}/${BPN}_${PV}${DPR}.dsc;md5sum=aefa3bba83230c609e45e23dc9a63143"

FILESPATH_append = ":${COREBASE}/meta/recipes-core/readline/files"
SRC_URI += "file://inputrc"
