PR = "${INC_PR}.0"

DPN = "wheel"

require wheel.inc

inherit setuptools python3native

DEPENDS += "python3-setuptools-native python3-native"

# Change install directory from "site-packages" to "dist-packages"
PYTHON_SITEPACKAGES_DIR = "${libdir}/${PYTHON_PN}/dist-packages"

do_install_append() {
	# Remove unwanted files
	find ${D}${libdir} -type f -name "*.pyc" -exec rm -f {} \;
	rm -rf ${D}${libdir}/${PYTHON_PN}/dist-packages/wheel-0.24.0-py3.4.egg-info/SOURCES.txt
}

BBCLASSEXTEND = "native"