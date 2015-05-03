#
# libcap_2.22.bb
#
require libcap.inc

PR = "r6"

SRC_URI[md5sum] = "b4896816b626bea445f0b3849bdd4077"
SRC_URI[sha256sum] = "e1cae65d8febf2579be37c255d2e058715785ead481a4e6a4357a06aff84721f"

#
# debian
#
inherit debian-package
DEBIAN_SECTION = "libs"
DPR = "0"
DPN = "libcap2"
