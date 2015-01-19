require recipes-devtools/make/make_4.0.bb

BPN = "make-dfsg"
inherit debian-package

DEBIAN_SECTION = "devel"
DPR = "0"

LICENSE = "GPLv3 & LGPLv2"
LIC_FILES_CHKSUM = " \
file://COPYING;md5=d32239bcb673463ab874e80d47fae504 \
file://tests/COPYING;md5=d32239bcb673463ab874e80d47fae504 \
file://glob/COPYING.LIB;md5=4a770b67e6be0f60da244beb2de0fce4 \
"
