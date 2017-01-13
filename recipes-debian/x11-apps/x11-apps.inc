SUMMARY = "X applications"

inherit debian-package
PV = "7.7+4"
DPN = "x11-apps"

LICENSE = "MIT-X"

DEPENDS = "virtual/libx11 libxmu libxaw xbitmaps libxt libxkbfile libxrender libxft libxcursor libpng"

# Apply patch by quilt
DEBIAN_PATCH_TYPE = "quilt"

inherit autotools pkgconfig distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

FILES_${PN} += "${libdir}/X11 ${datadir}/X11"
