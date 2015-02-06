# Don't re-use mklibs-native_0.1.38.bb 
# because function do_configure_prepend() cannot execute.
# Reason is ${PV} is in format "gitAUTOINC+id",
# therefore, command:
# 	sed "s+MKLIBS_VERSION+${PV}+" ${S}/configure.ac
# is failed.
#require recipes-devtools/mklibs/mklibs-native_0.1.38.bb

FILESEXTRAPATHS_prepend = "${COREBASE}/meta/recipes-devtools/mklibs/files:"

DEPENDS = "python-native dpkg-native"

inherit debian-package autotools gettext native pythonnative

DEBIAN_SECTION = "devel"
DPR = "0"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=98d31037b13d896e33890738ef01af64"

SRC_URI += " \
file://ac_init_fix.patch \
file://fix_STT_GNU_IFUNC.patch \
"

# ${PV} is in format gitAUTOINC+id
# so change command:	sed "s+MKLIBS_VERSION+${PV}+" ${S}/configure.ac
# to:			sed "s/MKLIBS_VERSION/${PV}/" ${S}/configure.ac
do_configure_prepend() {
        sed "s/MKLIBS_VERSION/${PV}/" ${S}/configure.ac
}
