require xorg-data-common.inc

DESCRIPTION = "Common X11 Bitmaps"
LICENSE = "MIT"
DEPENDS += "libxmu"


SRC_URI[archive.md5sum] = "7444bbbd999b53bec6a60608a5301f4c"
SRC_URI[archive.sha256sum] = "3671b034356bbc4d32d052808cf646c940ec8b2d1913adac51b1453e41aa1e9d"
#PR = "${INC_PR}.0"
#
# Meta-debian
#
PR = "r0"
LIC_FILES_CHKSUM = "file://COPYING;md5=dbd075aaffa4a60a8d00696f2e4b9a8f"
FILES_${PN} = "\
	${includedir}/X11 \
	/usr/share/"
DEBIAN_PATCH_TYPE = "nopatch"

PACKAGES = "${PN}"
