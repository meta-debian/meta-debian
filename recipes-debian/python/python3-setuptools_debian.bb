# base recipe: meta/recipes-devtools/python/python3-setuptools_40.8.0.bb
# base branch: warrior

require recipes-devtools/python/python-setuptools.inc

inherit debian-package
require recipes-debian/sources/python-setuptools.inc
DEBIAN_UNPACK_DIR = "${WORKDIR}/python-setuptools-${PV}"

FILESPATH_append = ":${COREBASE}/meta/recipes-devtools/python/files"
inherit setuptools3

do_install_append() {
	mv ${D}${bindir}/easy_install ${D}${bindir}/easy3_install
}
