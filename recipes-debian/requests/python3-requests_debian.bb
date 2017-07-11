PR = "${INC_PR}"

require requests.inc

inherit setuptools python3native

# Change install directory from "site-packages" to "dist-packages"
PYTHON_SITEPACKAGES_DIR = "${libdir}/${PYTHON_PN}/dist-packages"

do_install_append() {
	# Remove unwanted files
	find ${D}${libdir} -type f -name "*.pyc" -exec rm -f {} \;
	rm -rf ${D}${libdir}/${PYTHON_PN}/dist-packages/requests-2.4.3-py3.4.egg-info/SOURCES.txt
}
RDEPENDS_${PN} += "ca-certificates python3-chardet python3-urllib3"
