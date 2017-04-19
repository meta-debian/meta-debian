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

# Change install directory from "site-packages" to "dist-packages"
PYTHON_SITEPACKAGES_DIR = "${libdir}/${PYTHON_PN}/dist-packages"

do_install_append() {
	# Remove unwanted files
	find ${D}${libdir} -type f -name "*.pyc" -exec rm -f {} \;
	rm -rf ${D}${libdir}/${PYTHON_PN}/dist-packages/urllib3-1.9.1-py3.4.egg-info/SOURCES.txt
}

BBCLASSEXTEND = "native"