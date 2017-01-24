SUMMARY = "HTTP library with thread-safe connection pooling for Python"

PR = "${INC_PR}.0"

require python-urllib3.inc

inherit setuptools 

DEPENDS += "six-native"

# Change install directory from "site-packages" to "dist-packages"
PYTHON_SITEPACKAGES_DIR = "${libdir}/${PYTHON_DIR}/dist-packages"

do_install_append() {
	# Remove unwanted files
	find ${D}${libdir} -type f -name "*.pyc" -exec rm -f {} \;
	rm -rf ${D}${libdir}/${PYTHON_DIR}/dist-packages/urllib3-1.9.1-py2.7.egg-info/SOURCES.txt
}

BBCLASSEXTEND = "native"
