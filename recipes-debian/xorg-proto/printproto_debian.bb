require xorg-proto-common.inc

PR = "${INC_PR}.0"
DPN = "x11proto-print"

LIC_FILES_CHKSUM = "file://COPYING;md5=658463213f19b48b81f8672d2696069f"

# no patch found
DEBIAN_PATCH_TYPE = "nopatch"


# Ship package follow debian
PACKAGES = "x11proto-print-dev x11proto-print-doc"

FILES_x11proto-print-dev = "${libdir} ${includedir}"
FILES_x11proto-print-doc = "${datadir}"

