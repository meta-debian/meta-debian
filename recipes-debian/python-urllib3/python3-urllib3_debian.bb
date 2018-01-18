SUMMARY = "HTTP library with thread-safe connection pooling for Python3"

PR = "${INC_PR}.0"

DPN = "python-urllib3"

require python-urllib3.inc

inherit setuptools python3native

DEPENDS += "python3-six python3-setuptools-native"
RDEPENDS_${PN}_class_target += "\
	${PYTHON_PN}-email \
	${PYTHON_PN}-netclient \
	${PYTHON_PN}-threading "

do_install_append() {
	# Remove unwanted files
	find ${D}${PYTHON_SITEPACKAGES_DIR} -type f -name "*.pyc" -exec rm -f {} \;
	rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/urllib3-1.9.1-py3.4.egg-info/SOURCES.txt
}

BBCLASSEXTEND = "native"
