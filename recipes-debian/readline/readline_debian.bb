require recipes-core/readline/readline_6.3.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-core/readline/readline-6.3:\
${COREBASE}/meta/recipes-core/readline/files:\
"

inherit debian-package
DEBIAN_SECTION = "libs"
DPR = "0"

BPN = "readline6"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

# Fix QA issue file not shipped to any package
FILES_${PN} += "${datadir}"

# Patch file norpath.patch and readline-dispatch-multikey.patch
# no need to applied since it has been applied in new version 
# of source code already.
SRC_URI += "\
	file://configure-fix.patch\
	file://acinclude.m4\
	file://readline63-003\
"

# Remove patch which has already applied in new version of source code
SRC_URI_remove = "file://readline-dispatch-multikey.patch"

