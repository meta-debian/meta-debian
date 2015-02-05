require recipes-support/libxslt/libxslt_1.1.28.bb

inherit debian-package
DEBIAN_SECTION = "text"
DPR = "0"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://Copyright;md5=0cd9a07afbeb24026c9b03aecfeba458"

# Patch file pkgconfig_fix.patch can not apply, new
# pkgconfig_fix.patch has been created with the same
# purpose.
SRC_URI += "\
	file://pkgconfig_fix.patch\
"
