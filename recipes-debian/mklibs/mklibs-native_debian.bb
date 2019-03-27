SUMMARY = "Shared library optimisation tool"
DESCRIPTION = "mklibs produces cut-down shared libraries that contain only the routines required by a particular set of executables."
HOMEPAGE = "https://launchpad.net/mklibs"
SECTION = "devel"
LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=98d31037b13d896e33890738ef01af64"

inherit debian-package
require recipes-debian/sources/mklibs.inc
FILESPATH_append = ":${COREBASE}/meta/recipes-devtools/mklibs/files"

SRC_URI += " \
	file://ac_init_fix.patch\
	file://fix_STT_GNU_IFUNC.patch\
	file://sysrooted-ldso.patch \
	file://avoid-failure-on-symbol-provided-by-application.patch \
	file://show-GNU-unique-symbols-as-provided-symbols.patch \
	file://fix_cross_compile.patch \
"

UPSTREAM_CHECK_URI = "${DEBIAN_MIRROR}/main/m/mklibs/"

inherit autotools gettext native
