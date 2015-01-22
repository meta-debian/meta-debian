require recipes-devtools/ossp-uuid/ossp-uuid_1.6.2.bb
FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-devtools/ossp-uuid/ossp-uuid:"

inherit debian-package

DEBIAN_SECTION = "libs"
DPR = "0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = " \
file://README;beginline=30;endline=55;md5=b394fadb039bbfca6ad9d9d769ee960e \
file://uuid_md5.c;beginline=1;endline=28;md5=9c1f4b2218546deae24c91be1dcf00dd \
"

# Remove patches from SRC_URI:
# file://0001-Change-library-name.patch,
# file://0002-uuid-preserve-m-option-status-in-v-option-handling.patch,
# file://0003-Fix-whatis-entries.patch,
# file://0004-fix-data-uuid-from-string.patch.
# Because these patches are already in debian/patches directory.
SRC_URI += " \
file://uuid-libtool.patch \
file://uuid-nostrip.patch \
file://install-pc.patch \
"
