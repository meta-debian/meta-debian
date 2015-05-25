require xorg-lib-common.inc
DESCRIPTION = "X Printing Extension (Xprint) client library"
DEPENDS += "libxext libxau printproto"
#PE = "1"
#PR = "${INC_PR}.0"

SRC_URI[archive.md5sum] = "7ae1d63748e79086bd51a633da1ff1a9"
SRC_URI[archive.sha256sum] = "71d1f260005616d646b8c8788365f2b7d93911dac57bb53b65753d9f9e6443d2"

CFLAGS_append += " -I ${S}/include/X11/XprintUtil -I ${S}/include/X11/extensions"

XORG_PN = "libXp"
#
# Meta-debian
#
inherit debian-package
DPR = "0"
DEBIAN_PATCH_TYPE = "quilt"
LIC_FILES_CHKSUM = "file://COPYING;md5=9504a1264f5ddd4949254a57c0f8d6bb"
