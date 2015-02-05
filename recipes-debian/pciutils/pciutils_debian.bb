require recipes-bsp/pciutils/pciutils_3.2.1.bb
FILESEXTRAPATHS_prepend = "\
${COREBASE}/meta/recipes-bsp/pciutils/pciutils:\
"

inherit debian-package
DEBIAN_SECTION = "admin"
DPR = "0"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

# Patch configure.patch can not apply so
# file configure-edited.patch was created for apply
# with the same purpose.
# Patch file add_libkmod.patch was created for adding linker to 
# libkmod because of underfine reference error when bitbake.
# Patch file add-install-dir-for-udeb.patch was created for
# creating install dir of udeb.
SRC_URI += "\
	file://configure-edited.patch\
	file://lib-build-fix-edited.patch\
	file://guess-fix-edited.patch\
	file://makefile-edited.patch\
	file://add_libkmod.patch\
	file://add-install-dir-for-udeb.patch\
"
