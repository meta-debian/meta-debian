#
# Base recipe: meta-oe/recipes-graphics/xorg-data/xbitmaps_1.1.1.bb
# Base branch: master
# Base commit: d523cc183903fd0bc2e53af6af99a88ea1652e0d

HOMEPAGE = "http://www.x.org"
DESCRIPTION = "Common X11 Bitmaps"

PR = "r0"

inherit debian-package
PV = "1.1.1"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=dbd075aaffa4a60a8d00696f2e4b9a8f"

inherit autotools pkgconfig

DEPENDS += "libxmu"

PACKAGES = "${PN}"
FILES_${PN} = "${includedir} ${datadir}"

# There is no patch file
DEBIAN_PATCH_TYPE = "nopatch"
