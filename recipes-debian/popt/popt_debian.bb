#
# base recipe: meta/recipes-support/popt/popt_1.16.bb
# base branch: master
# base commit: a5d1288804e517dee113cb9302149541f825d316
#
SUMMARY = "Library for parsing command line options"
HOMEPAGE = "http://rpm5.org/"

inherit debian-package
PV = "1.16"
DPR = "-11"
DSC_URI = "${DEBIAN_MIRROR}/main/p/${BPN}/${BPN}_${PV}${DPR}.dsc;md5sum=bc1f4856cd95dcd872e4bbf66b098d14"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=cb0613c30af2a8249b8dcc67d3edb06d"

DEPENDS = "virtual/libiconv"

inherit autotools gettext

BBCLASSEXTEND = "native nativesdk"
