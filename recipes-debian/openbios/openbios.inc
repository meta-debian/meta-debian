DESCRIPTION = "OpenBIOS is a free, portable implementation of IEEE 1275-1994 (Open Firmware)"
HOMEPAGE = "https://github.com/openbios"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=6f96a62bfc31d872bee045fc9ca3564f"

inherit debian-package
require recipes-debian/sources/openbios.inc

BPN = "openbios"
