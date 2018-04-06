SUMMARY = "Python Distutils Enhancements"
DESCRIPTION = "Extensions to the python-distutils for large or complex distributions."
HOMEPAGE = "https://pypi.python.org/pypi/setuptools"

INC_PR = "r0"

inherit debian-package
PV = "5.5.1"

LICENSE = "Python-2.0 | ZPL-2.1"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=d933a991eaea0e21162565f1736b2fa2"

KEEP_NONARCH_BASELIB = "1"

inherit distutils

DISTUTILS_INSTALL_ARGS += " \
    --root='${D}' \
    --install-layout=deb \
    --prefix='${prefix}' \
    --install-lib='${PYTHON_SITEPACKAGES_DIR}' \
    --install-data='${datadir}' \
"
# required by distutils/sysconfig.py to get config dir
export DEB_HOST_MULTIARCH

do_install_append() {
	# Remove unwanted files
	find ${D}${PYTHON_SITEPACKAGES_DIR} -type f -name "*.pyc" -exec rm -f {} \;

	# Install setuptools.pth
	SETUPTOOLSVER=$(head -n 1 debian/changelog \
	              | sed 's|.*(\([^()]*\)).*|\1|' \
	              | sed 's|-.*||')
	echo "setuptools-${SETUPTOOLSVER}.egg-info" \
	     > ${D}${PYTHON_SITEPACKAGES_DIR}/setuptools.pth
}

BBCLASSEXTEND = "native nativesdk"
