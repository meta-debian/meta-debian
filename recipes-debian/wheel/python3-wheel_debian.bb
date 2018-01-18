PR = "${INC_PR}.0"

DPN = "wheel"

require wheel.inc

inherit setuptools python3native

DEPENDS += "python3-setuptools-native python3-native"

do_install_append() {
	# Remove unwanted files
	find ${D}${PYTHON_SITEPACKAGES_DIR} -type f -name "*.pyc" -exec rm -f {} \;
	rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/wheel-0.24.0-py3.4.egg-info/SOURCES.txt
}

BBCLASSEXTEND = "native"
