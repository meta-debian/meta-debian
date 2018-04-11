# base recipe: meta/recipes-extended/cracklib/cracklib_2.9.1.bb
# base branch: master

SUMMARY = "Password strength checker library"
HOMEPAGE = "http://sourceforge.net/projects/cracklib"

PR = "r0"

DPN = "cracklib2"

inherit debian-package
PV = "2.9.2"

LICENSE = "LGPLv2.1+"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=e3eda01d9815f8d24aae2dbd89b68b06"

DEPENDS = "cracklib-native zlib python"
RDEPENDS_python-cracklib += "python"

EXTRA_OECONF = "--with-python --libdir=${libdir}"

# These variables use to set pythondir and pyexecdir to ${PYTHON_SITEPACKAGES_DIR}
# Prevent python modules install wrong path when build nativesdk
CACHED_CONFIGUREVARS += "am_cv_python_pythondir=${PYTHON_SITEPACKAGES_DIR} \
                         am_cv_python_pyexecdir=${PYTHON_SITEPACKAGES_DIR}"

inherit autotools gettext pythonnative python-dir

# required by distutils/sysconfig.py to get config dir
export DEB_HOST_MULTIARCH

do_install_append_class-target() {
	create-cracklib-dict -o ${D}${datadir}/cracklib/pw_dict \
				${D}${datadir}/cracklib/cracklib-small
}

do_install_append() {
	src_dir="${D}${PYTHON_SITEPACKAGES_DIR}"
	rm -f $src_dir/*.pyo
	
	ln -sf libcrack.so.2 ${D}${libdir}/libcrack.so

	install -d -m 0755 ${D}${sysconfdir}/cracklib
	cp ${S}/debian/cracklib.conf ${D}${sysconfdir}/cracklib/
	
	install -d ${D}${sysconfdir}/cron.daily/
	install -m 0755 ${S}/debian/cracklib-runtime.cron.daily \
			${D}${sysconfdir}/cron.daily/cracklib-runtime

	install -m 0755 ${S}/debian/update-cracklib ${D}${sbindir}/
}

BBCLASSEXTEND = "native nativesdk"

PACKAGE_BEFORE_PN = "${PN}-runtime python-cracklib"

FILES_python-cracklib = "${PYTHON_SITEPACKAGES_DIR}/*"
FILES_${PN}-dbg += "${PYTHON_SITEPACKAGES_DIR}/.debug"
FILES_${PN}-runtime = " \
    ${sysconfdir}/* \
    ${sbindir}/* \
    ${datadir} \
"
FILES_${PN}-staticdev += "${PYTHON_SITEPACKAGES_DIR}/*.a"

RDEPENDS_${PN}-runtime += "${PN}"
RDEPENDS_python-cracklib += "${PN}-runtime"

DEBIANNAME_${PN}-dev = "libcrack2-dev"
DEBIAN_NOAUTONAME_${PN}-runtime = "1"
DEBIAN_NOAUTONAME_python-cracklib = "1"

RPROVIDES_${PN} += "libcrack"
RPROVIDES_${PN}-dev += "libcrack-dev"
