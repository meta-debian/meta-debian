require jinja2.inc
PR = "${INC_PR}.0"

do_install_append() {
	install -D -m 0644 ${S}/ext/Vim/jinja.vim \
		${D}${datadir}/vim/addons/syntax/jinja.vim
	install -D -m 0644 ${S}/debian/jinja.yaml \
		${D}${datadir}/vim/registry/jinja.yaml
}
FILES_${PN} += "${datadir}/vim"
RDEPENDS_${PN} += "python-markupsafe"
RRECOMMENDS_${PN} += "python-pkg-resources"
