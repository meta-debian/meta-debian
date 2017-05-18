SUMMARY = "Python Distutils Enhancements"
DESCRIPTION = "Extensions to the python-distutils for large or complex distributions."
HOMEPAGE = "https://pypi.python.org/pypi/setuptools"

INC_PR = "r0"

inherit debian-package
PV = "5.5.1"

LICENSE = "Python-2.0 | ZPL-2.1"
LIC_FILES_CHKSUM = "file://PKG-INFO;beginline=8;endline=8;md5=d933a991eaea0e21162565f1736b2fa2"

inherit distutils

DISTUTILS_INSTALL_ARGS += " \
    --root='${D}' \
    --install-layout=deb \
    --prefix='${prefix}' \
    --install-lib='${libdir}/${PYTHON_DIR}/dist-packages' \
    --install-data='${datadir}' \
"
# need to export these variables for python runtime
# fix error:
#       PREFIX = os.path.normpath(sys.prefix).replace( os.getenv("BUILD_SYS"), os.getenv("HOST_SYS") )
#       TypeError: Can't convert 'NoneType' object to str implicitly
export BUILD_SYS
export HOST_SYS

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

BBCLASSEXTEND = "native nativesdk"
