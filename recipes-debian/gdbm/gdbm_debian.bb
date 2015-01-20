require recipes-support/gdbm/gdbm_1.8.3.bb

inherit debian-package
DEBIAN_SECTION = "libs"

DPR = "0"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d8e20eece214df8ef953ed5857862150"

# Two patch files in reused recipes can not apply to 
# current version of gdbm on git. So makefile.patch
# was created with the same purpose as two patch files
# from reused recipes.
#
# This patch changes INSTALL_ROOT to DESTDIR and
# remove link to gdbm.
SRC_URI += "\
file://makefile.patch \
"
