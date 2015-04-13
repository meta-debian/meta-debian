require recipes-core/base-passwd/base-passwd_3.5.29.bb
FILESEXTRAPATHS_prepend = "\
${THISDIR}/files:${COREBASE}/meta/recipes-core/base-passwd/base-passwd:\
"

inherit debian-package
DEBIAN_SECTION = "admin"
DPR = "0"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=eb723b61539feef013de476e68b5c50a"

# Patch nobash.patch, noshadow.patch and disable-docs.patch
# can not apply, so nobash_edited.patch, noshadow_edited.patch
# and disable-docs_edited.patch were created with same purpose.
#
# Patch remove_cdebconf.patch remove all related part of libdebconf
# in base-passwd because libdebconf was not supported and the function
# related to libdebconf is not important.
SRC_URI += "\
	file://add_shutdown.patch\
	file://nobash_edited.patch\
	file://noshadow_edited.patch\
	file://input.patch\
	file://disable-docs_edited.patch\
	file://remove_cdebconf.patch\
"
