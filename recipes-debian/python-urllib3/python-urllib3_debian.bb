SUMMARY = "HTTP library with thread-safe connection pooling for Python"

PR = "${INC_PR}.0"

require python-urllib3.inc

inherit setuptools 

DEPENDS += "six-native"

do_install_append() {
	# Remove unwanted files
	find ${D}${PYTHON_SITEPACKAGES_DIR} -type f -name "*.pyc" -exec rm -f {} \;
	rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/urllib3-1.9.1-py2.7.egg-info/SOURCES.txt
}

BBCLASSEXTEND = "native"
