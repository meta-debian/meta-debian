SUMMARY = "library for program launch feedback"
DESCRIPTION = "startup-notification is a library which allows programs to give the user \
visual feedback that they are being launched; this is typically implemented \
using a busy cursor. This library is currently used by GNOME programs, but \
is part of the freedesktop.org suite of cross-desktop libraries."

inherit debian-package
PV = "0.12"

LICENSE = "LGPLv2+ & MIT"
LIC_FILES_CHKSUM = " \
    file://COPYING;md5=a2ae2cd47d6d2f238410f5364dfbc0f2 \
    file://libsn/sn-util.c;endline=18;md5=18a14dc1825d38e741d772311fea9ee1 \
    file://libsn/sn-util.h;endline=23;md5=6d05bc0ebdcf5513a6e77cb26e8cd7e2 \
"

DEPENDS = "virtual/libx11 xcb-util"

# source format is 3.0 but there is no patch
DEBIAN_QUILT_PATCHES = ""
# debian/patches folder exists but empty
# remove it to pass do_debian_patch
do_debian_patch_prepend() {
	if [ "$(ls ${DEBIAN_UNPACK_DIR}/debian/patches)" = "" ]; then
		rm -rf ${DEBIAN_UNPACK_DIR}/debian/patches
	fi
}

inherit autotools distro_features_check pkgconfig

REQUIRED_DISTRO_FEATURES = "x11"

DEBIANNAME_${PN} = "lib${DPN}0"
DEBIANNAME_${PN}-dev = "lib${DPN}0-dev"
RPROVIDES_${PN} += "lib${DPN}"
RPROVIDES_${PN}-dev += "lib${DPN}-dev"
