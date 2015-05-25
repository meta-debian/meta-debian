require xorg-driver-input.inc

SUMMARY = "X.Org X server -- mouse input driver"

DESCRIPTION = "mouse is an Xorg input driver for mice. The driver \
supports most available mouse types and interfaces.  The mouse driver \
functions as a pointer input device, and may be used as the X server's \
core pointer. Multiple mice are supported by multiple instances of this \
driver."

LIC_FILES_CHKSUM = "file://COPYING;md5=90ea9f90d72b6d9327dede5ffdb2a510"

SRC_URI[md5sum] = "36b5b92000c4644f648b58a535e4ee73"
SRC_URI[sha256sum] = "5d601e4bae53d5e9ead4ecd700f1beb5aeaf78b79e634c4aa381a9ce00276488"

#
# debian
#
inherit debian-package
DEBIAN_SECTION = "x11"
DPR = "0"
DPN = "xserver-xorg-input-mouse"
DEBIAN_PATCH_TYPE = "quilt"
debian_patch_quilt() {
        if [ ! -s ${DEBIAN_UNPACK_DIR}/debian/patches/series ]; then
                bbfatal "no patch in series"
        elif test $(ls ${DEBIAN_UNPACK_DIR}/debian/patches/ | wc -l) -ne 1; then
                QUILT_PATCHES=${DEBIAN_UNPACK_DIR}/debian/patches \
                        quilt --quiltrc /dev/null push -a
        fi
}
