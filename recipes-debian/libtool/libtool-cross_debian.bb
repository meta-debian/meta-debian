#
# base recipe: meta/recipes-devtools/libtool/libtool-cross_2.4.6.bb
# base branch: master
# base commit: b0f2f690a3513e4c9fa30fee1b8d7ac2d7140657
#

require libtool.inc

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
	sed -e 's@^\(predep_objects="\).*@\1"@' \
	    -e 's@^\(postdep_objects="\).*@\1"@' \
	    -i ${D}${bindir_crossscripts}/${HOST_SYS}-libtool
	sed -i '/^archive_cmds=/s/\-nostdlib//g' ${D}${bindir_crossscripts}/${HOST_SYS}-libtool
	sed -i '/^archive_expsym_cmds=/s/\-nostdlib//g' ${D}${bindir_crossscripts}/${HOST_SYS}-libtool
	GREP='/bin/grep' SED='sed' ${S}/build-aux/inline-source libtoolize > ${D}${bindir_crossscripts}/libtoolize
	chmod 0755 ${D}${bindir_crossscripts}/libtoolize
	install -d ${D}${target_datadir}/libtool/build-aux/
	install -d ${D}${target_datadir}/aclocal/
	install -c ${S}/build-aux/compile ${D}${target_datadir}/libtool/build-aux/
	install -c ${S}/build-aux/config.guess ${D}${target_datadir}/libtool/build-aux/
	install -c ${S}/build-aux/config.sub ${D}${target_datadir}/libtool/build-aux/
	install -c ${S}/build-aux/depcomp ${D}${target_datadir}/libtool/build-aux/
	install -c ${S}/build-aux/install-sh ${D}${target_datadir}/libtool/build-aux/
	install -c ${S}/build-aux/missing ${D}${target_datadir}/libtool/build-aux/
	install -c -m 0644 ${S}/build-aux/ltmain.sh ${D}${target_datadir}/libtool/build-aux/
	install -c -m 0644 ${S}/m4/*.m4 ${D}${target_datadir}/aclocal/
}

SYSROOT_DIRS += "${bindir_crossscripts} ${target_datadir}"

SSTATE_SCAN_FILES += "libtoolize *-libtool"

# Don't apply debian/patches/link_all_deplibs.patch
# This patch make libtool do not link all dependency libs when create shared 
# object archive and causes an error while link gettext's libraries although 
# the dependency flag was already added.
do_debian_patch_prepend() {
	sed -i -e "/link_all_deplibs/ d" ${S}/debian/patches/series
}
