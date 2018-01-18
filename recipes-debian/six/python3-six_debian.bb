require six.inc

DPN = "six"

PR = "${INC_PR}.0"

inherit setuptools python3native

DEPENDS += "python3-setuptools-native"

do_install_append() {
	# remove unwanted files
	rm -rf `find ${D}${PYTHON_SITEPACKAGES_DIR} -type d -name "__pycache__"`
	rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/six-1.8.0-py3.4.egg-info/SOURCES.txt
}

BBCLASSEXTEND = "native"
