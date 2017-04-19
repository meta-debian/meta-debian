SUMMARY = "Apply a diff file to an original"
DESCRIPTION = "Patch will take a patch file containing any of the four forms of difference listing produced by the diff program and apply those differences to an original file, producing a patched version."
HOMEPAGE = "http://savannah.gnu.org/projects/patch"

PR = "r0"
inherit debian-package pkgconfig autotools
PV = "2.7.5"

LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS += "ed"
