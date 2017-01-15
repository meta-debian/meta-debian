#
# No base recipe
#

DESCRIPTION = "X11 Present extension wire protocol"
HOMEPAGE = "http://www.X.org"

require xorg-proto-common.inc

PR = "r0"

inherit debian-package
PV = "1.0"

DPN = "x11proto-present"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://presentproto.h;md5=b14629122d4550bc359a3a79ddafb152"

# There is no Debian's patch file
DEBIAN_PATCH_TYPE = "nopatch"

# Rename package follow Debian
PKG_${PN}-dev = "x11proto-present-dev"
