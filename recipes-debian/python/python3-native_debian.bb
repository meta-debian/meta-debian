#
# base recipe: meta/recipes-devtools/python/python3-native_3.4.3.bb
# base branch: jethro
# 

require python3.inc

PR = "${INC_PR}"

DEBIAN_PATCH_TYPE = "quilt"
EXTRANATIVEPATH += "bzip2-native"
DEPENDS = "openssl-native bzip2-replacement-native zlib-native readline-native sqlite3-native"

inherit native

RPROVIDES += "python3-distutils-native python3-compression-native python3-textutils-native python3-core-native"

EXTRA_OECONF_append = " --bindir=${bindir}/${PN} --without-ensurepip"

SRC_URI += " \
	file://python-config.patch \
	file://000-cross-compile.patch \
	file://020-dont-compile-python-files.patch \
	file://030-fixup-include-dirs.patch \
	file://070-dont-clean-ipkg-install.patch \
	file://080-distutils-dont_adjust_files.patch \
	file://110-enable-zlib.patch \
	file://130-readline-setup.patch \
	file://150-fix-setupterm.patch \
	file://python-3.3-multilib.patch \
	file://03-fix-tkinter-detection.patch \
	file://avoid_warning_about_tkinter.patch \
	file://shutil-follow-symlink-fix.patch \
	file://0001-h2py-Fix-issue-13032-where-it-fails-with-UnicodeDeco.patch \
	file://sysroot-include-headers.patch \
	file://unixccompiler.patch \
	file://makerace.patch \
	${DISTRO_SRC_URI} \
	file://sysconfig.py-add-_PYTHON_PROJECT_SRC.patch \
	file://setup.py-check-cross_compiling-when-get-FLAGS.patch \
"

EXTRA_OEMAKE = '\
	BUILD_SYS="" \
	HOST_SYS="" \
	LIBC="" \
	STAGING_LIBDIR=${STAGING_LIBDIR_NATIVE} \
	STAGING_INCDIR=${STAGING_INCDIR_NATIVE} \
	LIB=${baselib} \
	ARCH=${TARGET_ARCH} \
'

# No ctypes option for python 3
PYTHONLSBOPTS = ""

do_configure_append() {
	autoreconf --verbose --install --force --exclude=autopoint ${S}/Modules/_ctypes/libffi
}

do_install() {
	install -d ${D}${libdir}/pkgconfig
	oe_runmake 'DESTDIR=${D}' install
	if [ -e ${WORKDIR}/sitecustomize.py ]; then
		install -m 0644 ${WORKDIR}/sitecustomize.py ${D}/${libdir}/python${PYTHON_MAJMIN}
	fi
	install -d ${D}${bindir}/${PN}
	install -m 0755 Parser/pgen ${D}${bindir}/${PN}

	# Make sure we use /usr/bin/env python
	for PYTHSCRIPT in `grep -rIl ${bindir}/${PN}/python ${D}${bindir}/${PN}`; do
		sed -i -e '1s|^#!.*|#!/usr/bin/env python3|' $PYTHSCRIPT
	done
}
