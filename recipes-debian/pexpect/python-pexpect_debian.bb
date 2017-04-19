SUMMARY = "Python module for automating interactive applications"
DISCRIPTION = "\
	Pexpect is a pure Python module for spawning child applications; \
	controlling them; and responding to expected patterns in their \
	output. Pexpect works like Don Libes' Expect. Pexpect allows your \
	script to spawn a child application and control it as if a human were \
	typing commands."
HOMEPAGE = "http://www.noah.org/wiki/Pexpect"

PR = "${INC_PR}.0"

require pexpect.inc

inherit distutils

RDEPENDS_${PN}_class-target = "\
	python-core \
	python-io \
	python-terminal \
	python-resource \
	python-fcntl"

# Change install directory from "site-packages" to "dist-packages"
PYTHON_SITEPACKAGES_DIR = "${libdir}/${PYTHON_DIR}/dist-packages"

do_install_append() {
	install -d ${D}/${datadir}/pyshared/
	install -m 644 ${S}/*.py ${D}/${datadir}/pyshared/

	cp -r ${S}/pexpect/ ${D}/${datadir}/pyshared/
	cp -r ${D}/${libdir}/${PYTHON_DIR}/dist-packages/*.egg-info ${D}/${datadir}/pyshared/

	# Remove unwanted files
	find ${D}${libdir} -type f -name "*.pyc" -exec rm -f {} \;
}

FILES_${PN} = "${datadir}/* ${libdir}/${PYTHON_DIR}/*"

BBCLASSEXTEND = "native"
