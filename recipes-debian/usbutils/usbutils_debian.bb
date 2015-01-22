require recipes-bsp/usbutils/usbutils_007.bb

inherit debian-package
DEBIAN_SECTION = "utils"
DPR = "0"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=94d55d512a9ba36caa9b7df079bae19f"

