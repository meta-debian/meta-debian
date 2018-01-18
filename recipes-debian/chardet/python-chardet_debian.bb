SUMMARY = "Universal character encoding detector for Python2"

PR = "${INC_PR}.0"

require chardet.inc

inherit setuptools pythonnative

DEPENDS += "python-setuptools-native"

RDEPENDS_${PN}_class-target = "python-pkg-resources python"

do_install_append() {
	# Remove unwanted files
	find ${D}${PYTHON_SITEPACKAGES_DIR} -type f -name "*.pyc" -exec rm -f {} \;
	rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/chardet-2.3.0-py2.7.egg-info/SOURCES.txt
}

BBCLASSEXTEND = "native"
