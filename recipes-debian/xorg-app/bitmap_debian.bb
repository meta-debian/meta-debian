require xorg-app-common.inc
DEPENDS += " libxmu libxaw xbitmaps libxt"
#PE = "1"
#PR = "${INC_PR}.0"

SRC_URI[archive.md5sum] = "0b7ceb0ded994dac645bb81e86ce69cc"
SRC_URI[archive.sha256sum] = "aa51b522fb2579042b1a7f1a5cea8b5582f2f88ea19459469fc19d61714a031e"
#
# Meta-debian
#
inherit debian-package
DPN = "x11-apps"
S = "${DEBIAN_UNPACK_DIR}/bitmap"
DPR = "r0"
DEBIAN_PATCH_TYPE = "quilt"
LIC_FILES_CHKSUM = "file://README;md5=63ccf60e4f37272dd4e0a43b9df8e339"
do_configure_prepend() {
	echo ${S}
}
