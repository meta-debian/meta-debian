require six.inc

DPN = "six"

PR = "${INC_PR}.0"

inherit setuptools python3native

DEPENDS += "python3-setuptools-native"

# Change install directory from "site-packages" to "dist-packages"
PYTHON_SITEPACKAGES_DIR = "${libdir}/${PYTHON_PN}/dist-packages"

do_install_append() {
	# remove unwanted files
	rm -rf `find ${D}${libdir} -type d -name "__pycache__"`
	rm -rf ${D}${libdir}/${PYTHON_PN}/dist-packages/six-1.8.0-py3.4.egg-info/SOURCES.txt
}

BBCLASSEXTEND = "native"