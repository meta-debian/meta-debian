PR = "${INC_PR}.0"

require requests.inc

inherit setuptools pythonnative

DEPENDS += "python-urllib3-native python-chardet-native"

do_install_append() {
	# Remove unwanted files
	find ${D}${PYTHON_SITEPACKAGES_DIR} -type f -name "*.pyc" -exec rm -f {} \;
	rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/requests-2.4.3-py2.7.egg-info/SOURCES.txt
}

RDEPENDS_${PN} += "ca-certificates python-chardet python-urllib3"
BBCLASSEXTEND = "native"
