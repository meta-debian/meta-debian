SUMMARY = "X server utilities"

inherit debian-package
PV = "7.7+3"
DPN = "x11-xserver-utils"

LICENSE = "MIT"

DEPENDS = "virtual/libx11 libxxf86vm libxt libxmu libxaw libxi libxrandr libxext xbitmaps libxcursor"

# Apply debian patch by quilt
DEBIAN_PATCH_TYPE = "quilt"

inherit autotools pkgconfig distro_features_check

REQUIRED_DISTRO_FEATURES = "x11"

FILES_${PN} += "${datadir}/X11"
