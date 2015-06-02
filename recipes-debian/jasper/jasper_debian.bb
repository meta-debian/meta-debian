#
# Meta-debian
#
DESCRIPTION = "The JasPer JPEG-2000 runtime library"
LICENSE = "Jasper-2.0"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=6fa5cfb08782fcab103fad4ebb2a01d7 \
		    file://LICENSE;md5=baa697d7510288a9cdcce9bd7edaf9bc"
SECTION = "graphics"
PR = "r0"

DEPENDS = "libjpeg6b"

EXTRA_OECONF += " --enable-shared=yes"

inherit autotools
inherit debian-package
DPR = "0"
