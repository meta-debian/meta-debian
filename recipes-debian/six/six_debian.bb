PR = "${INC_PR}.1"

require six.inc

inherit setuptools pythonnative

DEPENDS += "python-setuptools-native"

# Change install directory from "site-packages" to "dist-packages"
PYTHON_SITEPACKAGES_DIR = "${libdir}/${PYTHON_DIR}/dist-packages"

do_install_append() {
	# Remove unwanted files
	find ${D}${libdir} -type f -name "*.pyc" -exec rm -f {} \;
	rm -rf ${D}${libdir}/${PYTHON_DIR}/dist-packages/six-1.8.0-py2.7.egg-info/SOURCES.txt
}

PKG_${PN} = "python-${PN}"
RPROVIDES_${PN} += "python-${PN}"
BBCLASSEXTEND = "native"
