#
# Base recipe: meta/recipes-graphics/xorg-driver/xorg-driver-input.inc
# Base branch: daisy
#

include xorg-driver-common.inc

DEPENDS += "inputproto kbproto "

FILES_${PN} += " ${libdir}/xorg/modules/input/*.so"
FILES_${PN}-dbg += " ${libdir}/xorg/modules/input/.debug"
