#
# Meta-debian
#
DESCRIPTION = "The JasPer JPEG-2000 runtime library"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYRIGHT;md5=6fa5cfb08782fcab103fad4ebb2a01d7 \
		    file://LICENSE;md5=baa697d7510288a9cdcce9bd7edaf9bc"
SECTION = "graphics"
PR = "r1"

DEPENDS = "libjpeg-turbo"

EXTRA_OECONF += " --enable-shared=yes"

inherit autotools
inherit debian-package
PV = "1.900.1-debian1"

PACKAGES =+ "libjasper "
FILES_libjasper = "${libdir}/libjasper.so.*"

DEBIANNAME_${PN}-dev = "libjasper-dev"
DEBIANNAME_${PN} = "libjasper-runtime"

BBCLASSEXTEND = "native"
