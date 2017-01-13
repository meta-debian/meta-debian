#
# Base recipe: meta/recipes-graphics/xorg-proto/damageproto_1.2.1.bb
# Base branch: daisy
#

require xorg-proto-common.inc
PV = "1.2.1"

SUMMARY = "Xdamage: X Damage extension headers"

DESCRIPTION = "This package provides the wire protocol for the DAMAGE \
extension.  The DAMAGE extension allows applications to receive \
information about changes made to pixel contents of windows and \
pixmaps."

PR = "${INC_PR}.0"

LICENSE = "MIT-style"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=d5f5a2de65c3a84cbde769f07a769608 \
file://damagewire.h;endline=23;md5=4a4501a592dbc7de5ce89255e50d0296"

RCONFLICTS_${PN} = "damageext"
BBCLASSEXTEND = "native"

DPN = "x11proto-damage"

# There is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
