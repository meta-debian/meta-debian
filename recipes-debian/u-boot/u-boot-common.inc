HOMEPAGE = "http://www.denx.de/wiki/U-Boot/WebHome"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

inherit debian-package
require recipes-debian/sources/u-boot.inc
BPN = "u-boot"
