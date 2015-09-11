require xorg-app-common.inc

PR = "${INC_PR}.0"

LIC_FILES_CHKSUM = "file://README;md5=63ccf60e4f37272dd4e0a43b9df8e339"

DEPENDS += " libxmu libxaw xbitmaps libxt"

DPN = "x11-apps"

S = "${DEBIAN_UNPACK_DIR}/bitmap"

#There is no debian patch
DEBIAN_PATCH_TYPE = "quilt"
