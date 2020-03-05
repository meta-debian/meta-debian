# based on OE-Core
require recipes-kernel/linux-libc-headers/linux-libc-headers.inc

# use the same kernel source as linux_git.bb
inherit linux-src

PROVIDES += "linux-libc-headers"

# ${PN}-dev and ${MLPREFIX}${PN}-dev conflict because both of them
# provide same /usr/include. This makes do_populate_sdk fails because
# packagegroup-core-standalone-sdk-target rdepends on them.
PACKAGES =+ "${PN}-headers"
FILES_${PN}-headers = "${includedir}"
RRECOMMENDS_${PN}-dev += "${PN}-headers"
