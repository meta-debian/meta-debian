require ${COREBASE}/meta/recipes-extended/bash/bash.inc

inherit debian-package
require recipes-debian/sources/bash.inc
FILESPATH_append = ":${COREBASE}/meta/recipes-extended/bash/bash"

# GPLv2+ (< 4.0), GPLv3+ (>= 4.0)
LICENSE = "GPLv3+"
LIC_FILES_CHKSUM = "file://COPYING;md5=d32239bcb673463ab874e80d47fae504"

SRC_URI += " \
           file://execute_cmd.patch;striplevel=0 \
           file://mkbuiltins_have_stringize.patch \
           file://build-tests.patch \
           file://test-output.patch \
           file://fix-run-coproc-run-heredoc-run-execscript-run-test-f.patch \
           file://run-ptest \
           file://fix-run-builtins.patch \
           "

DEBUG_OPTIMIZATION_append_armv4 = " ${@bb.utils.contains('TUNE_CCARGS', '-mthumb', '-fomit-frame-pointer', '', d)}"
DEBUG_OPTIMIZATION_append_armv5 = " ${@bb.utils.contains('TUNE_CCARGS', '-mthumb', '-fomit-frame-pointer', '', d)}"

do_install_append() {
	install -d ${D}${sysconfdir}/skel
	install -m 0644 ${S}/debian/etc.bash.bashrc ${D}${sysconfdir}/bash.bashrc
	install -m 0644 ${S}/debian/skel.bashrc ${D}${sysconfdir}/skel/.bashrc
	install -m 0644 ${S}/debian/skel.profile ${D}${sysconfdir}/skel/.profile
	install -m 0644 ${S}/debian/skel.bash_logout ${D}${sysconfdir}/skel/.bash_logout
}

FILES_${PN} += "${sysconfdir}"

BBCLASSEXTEND = "nativesdk"
