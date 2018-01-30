SUMMARY = "Universal character encoding detector for Python3"

PR = "${INC_PR}.0"

require chardet.inc

inherit setuptools python3native

DEPENDS += "python3-setuptools-native python3-wheel"

do_install_append() {
	# remove unwanted files
	find ${D}${PYTHON_SITEPACKAGES_DIR} -type f -name "*.pyc" -exec rm -f {} \;
	rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/chardet-2.3.0-py3.4.egg-info/SOURCES.txt

	# follow debian/rules
	mv ${D}${bindir}/chardetect ${D}${bindir}/chardetect3 
}

#follow debian/control
RDEPENDS_${PN}_class-target += "python python3-pkg-resources"

BBCLASSEXTEND = "native"
