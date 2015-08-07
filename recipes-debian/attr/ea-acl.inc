#
# base recipe: meta/recipes-support/attr/ea-acl.inc
# base branch: daisy
#

# this build system is mostly shared by attr and acl

inherit debian-package
inherit autotools-brokensep gettext

# the package comes with a custom config.h.in, it cannot be
# overwritten by autoheader
export AUTOHEADER = "true"
EXTRA_OECONF = "INSTALL_USER=root INSTALL_GROUP=root"
EXTRA_OECONF_append_class-native = " --enable-gettext=no"

EXTRA_OEMAKE = "PKG_LIB_DIR=${base_libdir} PKG_DEVLIB_DIR=${libdir}"

do_install () {
	oe_runmake install install-lib install-dev DIST_ROOT="${D}"
}

PACKAGES =+ "lib${DPN} lib${DPN}-dev lib${DPN}-staticdev lib${DPN}-doc"

FILES_lib${DPN} = "${base_libdir}/lib*${SOLIBS}"

FILES_lib${DPN}-dev = "${includedir} \
                      ${libdir}/lib*${SOLIBSDEV} \
                      ${libdir}/lib*.la \
                      ${base_libdir}/lib*${SOLIBSDEV} \
                      ${base_libdir}/lib*.la"

FILES_lib${DPN}-staticdev = "${libdir}/*.a ${base_libdir}/*.a"

FILES_lib${DPN}-doc = "${mandir}/man2 \
                      ${mandir}/man3"

# Only append ldflags for target recipe and if USE_NLS is enabled
LDFLAGS_append_libc-uclibc_class-target = "${@['', ' -lintl '][(d.getVar('USE_NLS', True) == 'yes')]}"
EXTRA_OECONF_append_libc-uclibc_class-target = "${@['', ' --disable-gettext '][(d.getVar('USE_NLS', True) == 'no')]}"

fix_symlink () {
	if test "${libdir}" = "${base_libdir}" ; then
		return
	fi
	# Remove bad symlinks & create the correct symlinks
	if test -L ${libdir}/lib${DPN}.so ; then
		rm -rf ${libdir}/lib${DPN}.so
		ln -sf ${base_libdir}/lib${DPN}.so ${libdir}/lib${DPN}.so
	fi
	if test -L ${base_libdir}/lib${DPN}.a ; then
		rm -rf ${base_libdir}/lib${DPN}.a
		ln -sf ${libdir}/lib${DPN}.a ${base_libdir}/lib${DPN}.a
	fi
	if test -L  ${base_libdir}/lib${DPN}.la ; then
		rm -rf ${base_libdir}/lib${DPN}.la
		ln -sf ${libdir}/lib${DPN}.la ${base_libdir}/lib${DPN}.la
	fi
}
SSTATEPOSTINSTFUNCS_class-native += "fix_symlink"
