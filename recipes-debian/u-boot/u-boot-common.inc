HOMEPAGE = "http://www.denx.de/wiki/U-Boot/WebHome"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=30503fd321432fc713238f582193b78e"

inherit debian-package
require recipes-debian/sources/u-boot.inc
BPN = "u-boot"

DEPENDS = "bison-native"
