require recipes-core/coreutils/${PN}_8.22.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-core/coreutils/coreutils-8.22:\
"

inherit debian-package
DEBIAN_SECTION = "utils"
DPR = "0"

LICENSE = "GPLv3"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

# alway try to apply debian patches by quilt
DEBIAN_PATCH_TYPE = "quilt"

# Exclude fix-for-dummy-man-usage.patch
# Patch say that the --output option should be before final argument
# but in debian source code it have already give the correct format.
SRC_URI += " \
file://remove-usr-local-lib-from-m4.patch \                          
file://dummy_help2man.patch \                                        
"

# To fix new warning coming out of gcc 4.7+
SRC_URI += " \
file://fix_warning_lib_fnmatch.patch \
"
