require flask.inc
PR = "${INC_PR}.0"

inherit python3native
DEPENDS += "python3-setuptools-native"
do_install_append() {
	# remove unwanted files
	rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/flask/__pycache__
	rm -rf ${D}${PYTHON_SITEPACKAGES_DIR}/flask/ext
	
	install -d ${D}${datadir}/python3/dist
	install -m 0644 ${S}/debian/python3-flask.pydist \
		${D}${datadir}/python3/dist/python3-flask
}
RRECOMMENDS_${PN} += "python3-pkg-resources"
RDEPENDS_${PN} += "python3-itsdangerous python3-jinja2 python3-werkzeug"
