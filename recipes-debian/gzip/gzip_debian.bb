require recipes-extended/gzip/gzip.inc

inherit debian-package
PV = "1.6"
DPR = "-5"
DSC_URI = "${DEBIAN_MIRROR}/main/g/${BPN}/${BPN}_${PV}${DPR}.dsc;md5sum=69c052088bef252c8bee61bad41aef65"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = " \
	file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
	file://gzip.h;beginline=8;endline=20;md5=6e47caaa630e0c8bf9f1bc8d94a8ed0e \
"

PROVIDES_append_class-native = " gzip-replacement-native"

BBCLASSEXTEND = "native"
