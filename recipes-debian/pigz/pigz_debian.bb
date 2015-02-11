require recipes-extended/pigz/pigz_2.3.1.bb

inherit debian-package

DEBIAN_SECTION = "utils"
DPR = "0"

LICENSE = "Zlib"
LIC_FILES_CHKSUM = " \
file://README;md5=d9835b8537721e63621b30c67e1af3e3 \
file://pigz.c;beginline=7;endline=21;md5=a21d4075cb00ab4ca17fce5e7534ca95 \
"
