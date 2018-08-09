require recipes-devtools/quilt/quilt.inc

inherit debian-package
PV = "0.65"
DPR = "-1"
DSC_URI = "${DEBIAN_MIRROR}/main/q/${BPN}/${BPN}_${PV}${DPR}.dsc;md5sum=cf368da1cce6e2611c044af194a9ea15;sha256sum=25342a895f3d72f5e835349271b9c9c3f63de0d1365430aac2c520e785b56e55"

FILESPATH_append = ":${COREBASE}/meta/recipes-devtools/quilt/quilt"
SRC_URI += " \
        file://run-ptest \
        file://Makefile \
        file://test.sh \
        file://0001-tests-Allow-different-output-from-mv.patch \
"
