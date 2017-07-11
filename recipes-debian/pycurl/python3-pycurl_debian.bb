SUMMARY = "Python bindings to libcurl (Python 3)"
DISCRIPTION = "\
	This module provides the Python bindings to libcurl. Please refer to \
	the libcurl documentation available in libcurl4-gnutls-dev \
	Debian package. \
	NOTE: the SSL support is provided by GnuTLS. \
	This package contains PyCURL for Python 3."
HOMEPAGE = "http://pycurl.sourceforge.net"

PR = "${INC_PR}.0"

require pycurl.inc

inherit distutils python3native

# Change install directory from "site-packages" to "dist-packages"
PYTHON_SITEPACKAGES_DIR = "${libdir}/${PYTHON_PN}/dist-packages"

# Ensure the docstrings are generated as make clean will remove them
do_compile_prepend() {
	${STAGING_BINDIR_NATIVE}/${PYTHON_PN}-native/${PYTHON_PN} setup.py docstrings
}

do_install_append() {
	rm -rf ${D}${datadir}/share
	find ${D}${libdir} -type f -name "*.pyc" -exec rm -f {} \;
}
