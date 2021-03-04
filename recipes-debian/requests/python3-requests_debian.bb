PR = "${INC_PR}"

require requests.inc

inherit setuptools python3native

DEPENDS += "python3-urllib3-native python3-chardet-native"
do_install_append() {
	# Remove unwanted files
	find ${D}${PYTHON_SITEPACKAGES_DIR} -type f -name "*.pyc" -exec rm -f {} \;
	rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/requests-2.4.3-py3.4.egg-info/SOURCES.txt
}
RDEPENDS_${PN} += "ca-certificates python3-chardet python3-urllib3"
