HOMEPAGE = "http://www.denx.de/wiki/U-Boot/WebHome"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

inherit debian-package
PV = "2018.05+dfsg"
DPR = "-1"
BPN = "u-boot"
DSC_URI = "${DEBIAN_MIRROR}/main/u/${BPN}/${BPN}_${PV}${DPR}.dsc;md5sum=c35a26b6d6a21ad37dbe3068277b06c5"

DEBIAN_UNPACK_DIR = "${WORKDIR}/${BPN}-2018.05"
