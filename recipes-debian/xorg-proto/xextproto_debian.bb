#
# Base recipe: meta/recipes-graphics/xorg-proto/xextproto_7.3.0.bb
# Base branch: daisy
#
require xorg-proto-common.inc
PV = "7.3.0"

SUMMARY = "XExt: X Extension headers"

DESCRIPTION = "This package provides the wire protocol for several X \
extensions.  These protocol extensions include DOUBLE-BUFFER, DPMS, \
Extended-Visual-Information, LBX, MIT_SHM, MIT_SUNDRY-NONSTANDARD, \
Multi-Buffering, SECURITY, SHAPE, SYNC, TOG-CUP, XC-APPGROUP, XC-MISC, \
XTEST.  In addition a small set of utility functions are also \
available."

PR = "${INC_PR}.0"

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=86f273291759d0ba2a22585cd1c06c53"

inherit gettext

EXTRA_OECONF_append = " --enable-specs=no"

BBCLASSEXTEND = "native nativesdk"

DPN = "x11proto-xext"

# there is no debian patch
DEBIAN_PATCH_TYPE = "nopatch"
