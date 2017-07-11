SUMMARY = "Universal character encoding detector for Python2"

PR = "${INC_PR}.0"

require chardet.inc

inherit setuptools pythonnative

DEPENDS += "python-setuptools-native"

RDEPENDS_${PN}_class-target = "python-pkg-resources python"

# Change install directory from "site-packages" to "dist-packages"
PYTHON_SITEPACKAGES_DIR = "${libdir}/${PYTHON_DIR}/dist-packages"

do_install_append() {
	# Remove unwanted files
	find ${D}${libdir} -type f -name "*.pyc" -exec rm -f {} \;
	rm -rf ${D}${libdir}/${PYTHON_DIR}/dist-packages/chardet-2.3.0-py2.7.egg-info/SOURCES.txt
}

BBCLASSEXTEND = "native"
