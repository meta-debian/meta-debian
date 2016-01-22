#
# Base recipe: meta/recipes-devtools/libtool/libtool-cross_2.4.2.bb
# Base branch: daisy
#

require libtool.inc

PR = "${INC_PR}.1"

PACKAGES = ""
SRC_URI += " \
file://prefix.patch \
file://fixinstall.patch \
"

datadir = "${STAGING_DIR_TARGET}${target_datadir}"

do_configure_prepend () {
	# Remove any existing libtool m4 since old stale versions would break
	# any upgrade
	rm -f ${STAGING_DATADIR}/aclocal/libtool.m4
	rm -f ${STAGING_DATADIR}/aclocal/lt*.m4
}

do_install () {
	install -d ${D}${bindir_crossscripts}/
	install -m 0755 ${HOST_SYS}-libtool ${D}${bindir_crossscripts}/${HOST_SYS}-libtool
	install -d ${D}${bindir_crossscripts}/
	install -m 0755 libtoolize ${D}${bindir_crossscripts}/
	install -d ${D}${target_datadir}/libtool/config/
	install -d ${D}${target_datadir}/aclocal/
	install -c ${S}/libltdl/config/compile ${D}${target_datadir}/libtool/config/
	install -c ${S}/libltdl/config/config.guess ${D}${target_datadir}/libtool/config/
	install -c ${S}/libltdl/config/config.sub ${D}${target_datadir}/libtool/config/
	install -c ${S}/libltdl/config/depcomp ${D}${target_datadir}/libtool/config/
	install -c ${S}/libltdl/config/install-sh ${D}${target_datadir}/libtool/config/
	install -c ${S}/libltdl/config/missing ${D}${target_datadir}/libtool/config/
	install -c -m 0644 ${S}/libltdl/config/ltmain.sh ${D}${target_datadir}/libtool/config/
	install -c -m 0644 ${S}/libltdl/m4/*.m4 ${D}${target_datadir}/aclocal/
}

SYSROOT_PREPROCESS_FUNCS += "libtoolcross_sysroot_preprocess"

libtoolcross_sysroot_preprocess () {
	sysroot_stage_dir ${D}${bindir_crossscripts} ${SYSROOT_DESTDIR}${bindir_crossscripts}
	sysroot_stage_dir ${D}${target_datadir} ${SYSROOT_DESTDIR}${target_datadir}
}

SSTATE_SCAN_FILES += "libtoolize *-libtool"

export CONFIG_SHELL="/bin/bash"

# Don't apply debian/patches/link_all_deplibs.patch
# This patch make libtool do not link all dependency libs when create shared 
# object archive and causes an error while link gettext's libraries although 
# the dependency flag was already added.

do_debian_patch_prepend() {
	sed -i -e "/link_all_deplibs/ d" ${S}/debian/patches/series
}
