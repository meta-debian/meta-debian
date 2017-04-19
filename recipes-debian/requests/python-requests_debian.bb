PR = "${INC_PR}.0"

require requests.inc

inherit setuptools pythonnative

DEPENDS += "python-urllib3-native python-chardet-native"

# Change install directory from "site-packages" to "dist-packages"
PYTHON_SITEPACKAGES_DIR = "${libdir}/${PYTHON_DIR}/dist-packages"

do_install_append() {
	# Remove unwanted files
	find ${D}${libdir} -type f -name "*.pyc" -exec rm -f {} \;
	rm -rf ${D}${libdir}/${PYTHON_DIR}/dist-packages/requests-2.4.3-py2.7.egg-info/SOURCES.txt
}

RDEPENDS_${PN} += "ca-certificates python-chardet python-urllib3"
BBCLASSEXTEND = "native"
