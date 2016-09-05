SUMMARY = "Python Distutils Enhancements"
DESCRIPTION = "Extensions to the python-distutils for large or complex distributions."
HOMEPAGE = "https://pypi.python.org/pypi/setuptools"

PR = "r0"

inherit debian-package

LICENSE = "Python-2.0 | ZPL-2.1"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=d933a991eaea0e21162565f1736b2fa2"

PROVIDES = "python-distribute"

inherit distutils

DISTUTILS_INSTALL_ARGS += " \
    --root='${D}' \
    --install-layout=deb \
    --prefix='${prefix}' \
    --install-lib='${libdir}/${PYTHON_DIR}/dist-packages' \
    --install-data='${datadir}' \
"

do_install_append() {
	# Remove unwanted files
	find ${D}${libdir} -type f -name "*.pyc" -exec rm -f {} \;

	# Install setuptools.pth
	SETUPTOOLSVER=$(head -n 1 debian/changelog \
	              | sed 's|.*(\([^()]*\)).*|\1|' \
	              | sed 's|-.*||')
	echo "setuptools-${SETUPTOOLSVER}.egg-info" \
	     > ${D}${libdir}/${PYTHON_DIR}/dist-packages/setuptools.pth
}

PACKAGES =+ "python-pkg-resources"

FILES_python-pkg-resources = "${libdir}/${PYTHON_DIR}/*-packages/pkg_resources.py"

RDEPENDS_${PN}_class-target += "python-pkg-resources"

RREPLACES_${PN} = "python-distribute"
RPROVIDES_${PN} = "python-distribute"
RCONFLICTS_${PN} = "python-distribute"

BBCLASSEXTEND = "native nativesdk"
