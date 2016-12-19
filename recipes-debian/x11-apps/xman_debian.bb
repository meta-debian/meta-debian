require x11-apps.inc

DESCRIPTION = "a manual page browser"

LIC_FILES_CHKSUM = "file://COPYING;md5=20ffa1308d2bf5ee9bba5f06cf9cc17e"

S = "${DEBIAN_UNPACK_DIR}/xman"

# Avoid error "cannot check for file existence when cross compiling"
EXTRA_OECONF = "--without-manconfig"
