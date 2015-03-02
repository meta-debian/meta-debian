require recipes-kernel/linux-libc-headers/linux-libc-headers.inc

# Fetch source from github by default, see
# meta-debian/classes/kernel-checkout.bbclass for repository URI
inherit kernel-checkout

SRC_URI[md5sum] = "ef2aae810366085c474c17b8569b429c"
SRC_URI[sha256sum] = "8454c3efc5952ea5323a6667ab64e562520f577279803bd48596bf438e7b84c8"

B = "${S}"

LINUX_VERSION = "3.10.24"
LINUX_SRCREV = "linux-3.10.y-zynq-backport"

PROVIDES += "linux-libc-headers"
