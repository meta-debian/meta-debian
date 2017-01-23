SUMMARY = "Universal character encoding detector for Python3"

PR = "${INC_PR}.0"

require chardet.inc

inherit setuptools python3native

DEPENDS += "python3-setuptools-native python3-wheel"

# Change install directory from "site-packages" to "dist-packages"
PYTHON_SITEPACKAGES_DIR = "${libdir}/${PYTHON_PN}/dist-packages"

do_install_append() {
	# remove unwanted files
	find ${D}${libdir} -type f -name "*.pyc" -exec rm -f {} \;
	rm -rf ${D}${libdir}/${PYTHON_PN}/dist-packages/chardet-2.3.0-py3.4.egg-info/SOURCES.txt

	# follow debian/rules
	mv ${D}${bindir}/chardetect ${D}${bindir}/chardetect3 
}

#follow debian/control
RDEPENDS_${PN}_class-target += "python python3-pkg-resources"

BBCLASSEXTEND = "native"
