SUMMARY = "Python bindings to libcurl"
DISCRIPTION = "\
	This module provides the Python bindings to libcurl. Please refer to \
	the libcurl documentation available in libcurl4-gnutls-dev \
	Debian package. \
	NOTE: the SSL support is provided by GnuTLS. \
	This package contains PyCURL for Python 2."
HOMEPAGE = "http://pycurl.sourceforge.net"
 
PR = "${INC_PR}.0"

require pycurl.inc

inherit distutils

# Change install directory from "site-packages" to "dist-packages"
PYTHON_SITEPACKAGES_DIR = "${libdir}/${PYTHON_DIR}/dist-packages"

do_install_append() {
	rm -rf ${D}${datadir}/share
	find ${D}${libdir} -type f -name "*.pyc" -exec rm -f {} \;
}

BBCLASSEXTEND = "native"
