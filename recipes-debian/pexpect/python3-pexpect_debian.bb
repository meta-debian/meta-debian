SUMMARY = "Python 3 module for automating interactive applications"
DISCRIPTION = "\
	Pexpect is a pure Python 3 module for spawning child applications; \
	controlling them; and responding to expected patterns in their output.\
	Pexpect works like Don Libes' Expect. Pexpect allows your script to \
	spawn a child application and control it as if a human were \
	typing commands."
HOMEPAGE = "http://www.noah.org/wiki/Pexpect"

PR = "${INC_PR}.0"

require pexpect.inc

inherit distutils python3native

# Change install directory from "site-packages" to "dist-packages"
PYTHON_SITEPACKAGES_DIR = "${libdir}/${PYTHON_PN}/dist-packages"

do_install_append() {
	# Remove unwanted files
	find ${D}${libdir} -type f -name "*.pyc" -exec rm -f {} \;
}

