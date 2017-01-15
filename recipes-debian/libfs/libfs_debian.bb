SUMMARY = "X11 Font Services library"
DESCRIPTION = "libFS, the Font Services library, provides various functions useful to \
X11 font servers, and clients connecting to font servers.  It is not used \
outside of these implementations."
HOMEPAGE = "http://www.X.org"

inherit debian-package
PV = "1.0.6"

LICENSE = "MIT & MIT-style"
LIC_FILES_CHKSUM = "file://COPYING;md5=0763cdab49e33d2000689ab9ab9b6f95"

DEBIAN_PATCH_TYPE = "nopatch"

DEPENDS = "util-macros xproto fontsproto xtrans"

inherit autotools distro_features_check pkgconfig

REQUIRED_DISTRO_FEATURES = "x11"

DEBIANNAME_${PN}-dbg = "${PN}6-dbg"
