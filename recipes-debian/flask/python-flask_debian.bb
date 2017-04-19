require flask.inc
PR = "${INC_PR}.1"
do_install_append() {
	# base on debian/python-flask.pyinstall
	install -D -m 0644 ${S}/debian/__init__.py \
		${D}${PYTHON_SITEPACKAGES_DIR}/flaskext/__init__.py
	
	install -d ${D}${datadir}/python/dist
	install -m 0644 ${S}/debian/python-flask.pydist \
		${D}${datadir}/python/dist/python-flask
}
RRECOMMENDS_${PN} += "python-pkg-resources"
RDEPENDS_${PN} += "python-itsdangerous python-jinja2 python-werkzeug python-threading"
