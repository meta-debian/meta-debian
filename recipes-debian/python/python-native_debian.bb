#
# base recipe: meta/recipes-devtools/python/python-native_2.7.9.bb
# base branch: master
#

require python.inc

EXTRANATIVEPATH += "bzip2-native"
DEPENDS = "openssl-native bzip2-replacement-native zlib-native readline-native sqlite3-native gdbm-native"
PR = "${INC_PR}.4"

# revert_use_of_sysconfigdata.patch:
# 	In current version, python uses _sysconfigdata.build_time_vars[],
# 	which contains information from the HOST.
# 	This patch reverts this behavior and uses Python.h and Makefile to get information.
# add_site-packages_to_getsitepackages.patch:
# 	Append "/usr/lib/python<version>/site-packages" to sitepackages
# 	for looking for python modules.
SRC_URI += "\
	file://05-enable-ctypes-cross-build.patch \
	file://11-distutils-never-modify-shebang-line.patch \
	file://12-distutils-prefix-is-inside-staging-area_debian.patch \
	file://unixccompiler.patch \
	file://nohostlibs_debian.patch \
	file://multilib_debian.patch \
	file://add-md5module-support.patch \
	file://builddir.patch \
	file://parallel-makeinst-create-bindir.patch \
	file://revert_use_of_sysconfigdata.patch \
	file://avoid_parallel_make_races_on_pgen.patch \
	file://add_site-packages_to_getsitepackages.patch \
"

FILESEXTRAPATHS =. "${THISDIR}/${PN}:"

inherit native

RPROVIDES += "python-distutils-native python-compression-native python-textutils-native python-codecs-native python-core-native"

EXTRA_OECONF_append = " --bindir=${bindir}/${PN}"

EXTRA_OEMAKE = '\
	BUILD_SYS="" \
	HOST_SYS="" \
	LIBC="" \
	STAGING_LIBDIR=${STAGING_LIBDIR_NATIVE} \
	STAGING_INCDIR=${STAGING_INCDIR_NATIVE} \
'

do_configure_prepend() {
	# This fix is a workaround which relates to function get_makefile_filename
	# in ${S}/Lib/distutils/sysconfig.py
	#
	# debian/patches set LIBPL to "$(LIBP)/config-$(MULTIARCH)$(DEBUG_EXT)".
	# However, we cannot get LIBPL value by calling get_config_var() in function get_makefile_filename(),
	# because it causes "RuntimeError: maximum recursion depth exceeded" if apply revert_use_of_sysconfigdata.patch.
	#
	# So revert LIBPL to its original value,
	# and get Makefile path with a hardcode path lib_dir/config/Makefile.
	# (see the last hunk in 12-distutils-prefix-is-inside-staging-area_debian.patch)
	sed -i -e 's#^LIBPL=.*#LIBPL= $(LIBP)/config#g' ${S}/Makefile.pre.in
}

do_configure_append() {
	autoreconf --verbose --install --force --exclude=autopoint ${S}/Modules/_ctypes/libffi
}

do_install() {
	oe_runmake 'DESTDIR=${D}' install
	install -d ${D}${bindir}/${PN}
	install -m 0755 Parser/pgen ${D}${bindir}/${PN}

	# Make sure we use /usr/bin/env python
	for PYTHSCRIPT in `grep -rIl ${bindir}/${PN}/python ${D}${bindir}/${PN}`; do
		sed -i -e '1s|^#!.*|#!/usr/bin/env python|' $PYTHSCRIPT
	done

	# Add a symlink to the native Python so that scripts can just invoke
	# "nativepython" and get the right one without needing absolute paths
	# (these often end up too long for the #! parser in the kernel as the
	# buffer is 128 bytes long).
	ln -s python-native/python ${D}${bindir}/nativepython

	# We don't want modules in ~/.local being used in preference to those
	# installed in the native sysroot, so disable user site support.
	sed -i -e 's,^\(ENABLE_USER_SITE = \).*,\1False,' ${D}${libdir}/python${PYTHON_MAJMIN}/site.py
}
